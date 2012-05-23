
Webapp to visualize maven artifact dependencies graph.


Usage
============
The basic command line is : 

    $> mvn clean package jetty:run-exploded

_'run-exploded' is needed for resource filtering_

Repositories are defined through a variable named 'remote-repos' (context-param in the web.xml file).
'remote-repos' is a list of URLs, separated by ';'

So as to set the value of this variable, you can :
* use a maven profile (add -p<YourProfile> to the maven command line)
* or directly set system properties (add -Dremote-repos=<YourRepositories> to the maven command line)

Example : 

    $> mvn clean package jetty:run-exploded -Dremote-repos="http://myproxyserver/releases;http://myproxyserver/snapshots"
    
    