eureka-listener-tomcat
======================

## DESCRIPTION

[Eureka](https://github.com/Netflix/eureka) is a REST (Representational State Transfer) based service that is primarily used in the cloud for locating services for the purpose of load balancing and failover of middle-tier servers.

This is a sample Apache Tomcat Lifecycle Listener to register all the applications deployed in the tomcat with the Eureka server.



## CONFIGURATION

Eureka Application needs to register with the eureka server for their locality and to be identified by the Loadbalancers and other services. This Listner can be used to register/deregister the application deployed in the tomcat.

After building the project with maven please copy the jar with all dependencies in to the tomcat lib folder along with the eureka-client.properties and change the below propery with instance of deployed eureka server.

eureka.serviceUrl.default=http://server-ip/eureka/v2/

After copying the libraies in to the lib folder add EurekaListner to the tomcats Context.xml.

<Listener className="eurekaplugin.EurekaListner" />

Above lifecycle event will register and deregister the application with the eureka server based on the triggering of the event.

Similarly there are different listners for the other application servers and those Listners can be implemented in the same way to register the applications with the eureka server.
