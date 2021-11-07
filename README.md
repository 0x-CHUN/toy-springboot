# toy-springboot

A simple web framework like springboot.

## Features

* The IOC container and dependency injection are implemented, the Bean factory class is implemented in a singleton mode, the circular dependency problem is solved through the three-level cache, and the @Autowire and @Qualifier methods are supported for injection. 
* AOP (Aspect Oriented Programming) is implemented through JDK, CGLib dynamic proxy, and annotations are used to configure aspects. There are multiple annotations such as @Aspect, @Before, @After, @Order, etc., and pointcuts can be configured through @Pointcut.
* Built-in HTTP server written by Netty, supporting annotations commonly used by Spring MVC.
* Implemented ORM (Object Relational Mapping) function, built-in MySQL connector. By using the annotations @Select, @Update, @Insert, @Delete on the interface declared by @Mapper to declare the SQL statement to be executed, then use CGLib to generate a dynamic proxy object, and then execute the SQL statement through Executor using JDBC.
