# request-simulator
A Java program for producing multiple requests from a single JSON request. It generates unique requests each time which makes it different from all the available simulators.

**Configuration for Ubuntu 12.04**:
1. Change values for JAVA_OPTS in `/etc/environment` to update heap size for JVM.
2. Create a file setenv.sh in "$CATALINA_BASE/bin", same location as `catalina.sh`. For example in some cases, in `/usr/share/tomcat7/bin` or /`var/lib/tomcat7/bin`.
3. Add CATALINA_OPTS to setenv.sh to set memory values for Tomcat,
as `export CATALINA_OPTS"-Xms__m -Xmx__m"`. Or change values for `JAVA_OPTS` in /etc/default/tomcat7.
