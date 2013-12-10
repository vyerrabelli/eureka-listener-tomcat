package eurekaplugin;
import java.util.Arrays;
import java.util.EventObject;
import java.util.List;
import org.apache.catalina.Context;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.CloudInstanceConfig;
import com.netflix.appinfo.InstanceInfo.InstanceStatus;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryManager;
public class EurekaListner implements LifecycleListener {
	public static final Logger LOGGER = LoggerFactory.getLogger(EurekaListner.class);
	private static List<String> eurekaFilters;
	private static boolean eurekaFiltersLoaded;
	public void lifecycleEvent(LifecycleEvent event) {
		eurekaHandler(event);

	}

	private void eurekaHandler(LifecycleEvent event) {
		try{
			if (event.getLifecycle() instanceof Context && Lifecycle.AFTER_START_EVENT.equals(event.getType())) {
				Context context =  (Context)event.getLifecycle();
				String appName = getAppName( context.getName());
				try {
					if(!getEurekaFilters(context).contains(appName)){
						LOGGER.info("registering: "+appName);
						register(getAppName(context.getName()));
					}
				} catch (InterruptedException e) {
					LOGGER.error("Error occured while registering the Applications: "+e.getMessage());
				}
			}

			if (event.getLifecycle() instanceof Context && Lifecycle.STOP_EVENT.equals(event.getType())) {
				Context context = (Context) event.getLifecycle();	
				String appName = getAppName(context.getName());
				if(!getEurekaFilters(context).contains(appName)){
					LOGGER.info("deregistering "+getAppName(appName));
					deregister(appName);
				}
			}
		}catch(Exception e){
			LOGGER.error("Error occured while communicating with eureka: "+e.getMessage());
		}
	}

	private List<String> getEurekaFilters(Context context) {
		if(!eurekaFiltersLoaded)
			eurekaFilters =  Arrays.asList(context.getServletContext().getInitParameter("eureka-filters").split(","));
		return eurekaFilters;

	}

	private <T extends EventObject> String getAppName(String applicationName) {
		return applicationName!=null && applicationName.length() > 0 ? applicationName.substring(1) : applicationName;
	}

	public static void register(String appName) throws InterruptedException {
		initDiscoveryManager(appName);
		registerInstanceWithEureka();
	}
	private static void registerInstanceWithEureka() throws InterruptedException {
		ApplicationInfoManager.getInstance().setInstanceStatus(InstanceStatus.UP);
		//	Thread.sleep(100000);
	}
	public static void deregister(String appName) {
		initDiscoveryManager(appName);
		shutdownComponent();

	}
	private static void shutdownComponent() {
		DiscoveryManager.getInstance().shutdownComponent();
	}
	private static void initDiscoveryManager(String appName) {
		ApplicationInfoManager.getInstance().initComponent(new CustomCloudConfig(appName));
		DiscoveryManager.getInstance().initComponent(new CustomCloudConfig(appName), new DefaultEurekaClientConfig());	
	}

}
class CustomCloudConfig extends CloudInstanceConfig{
	private String appName;
	public CustomCloudConfig(String appName) {
		this.appName = appName;
	}
	public String getAppname() {
		return appName;
	}
}
