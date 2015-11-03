**Contents**


# Introduction #

The `Bean` annotation is a specialization of the [Component](Component.md) annotation used to indicate that a class is a dependency-injection bean, meaning it is a candidate for component scanning by the framework if classpath scanning is enabled.

The annotation has a single, optional attribute which indicates the name of the bean. If it is not specified, the bean takes the class name in camelcase form.

Beans that are picked up during component scanning are registered with InfinitumContext and stored in its `BeanFactory`.

The scope of a bean, e.g. prototype or singleton, can be specified using the [Scope](Scope.md) annotation. The bean will have a singleton scope by default if not specified.

# Bean Example #

The below example illustrates the usage of the `Bean` annotation. The annotation will allow the bean to be picked up and registered automatically by the framework if component scanning is enabled.

```
@Bean
public class MyBean {

}
```