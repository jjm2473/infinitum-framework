**Contents**


# Introduction #

The `BeanFactoryPostProcessor` interface allows modifications to be made to an `InfinitumContext`'s bean definitions by exposing its `BeanFactory`. The interface has but a single method which must be implemented, `postProcessBeanFactory(BeanFactory beanFactory)`, which can be used to modify the context's `BeanFactory` after it has been initialized. This is useful for targeting environments which require different components. For example, if a component implementation only supports a particular set of devices or API levels, it could be overridden using a `BeanFactoryPostProcessor` which supplies an alternate implementation. This moves the code needed to swap implementations away from any application/business logic and allows Infinitum to handle an application's configuration.

`BeanFactoryPostProcessors` are specialized beans. As such, they can be registered with an InfinitumContext using normal means, either through XML or with annotations (if component scanning is enabled). This also means they are eligible for autowiring and they themselves can be autowire candidates. Each post processor's `postProcessBeanFactory` method is executed immediately after context initialization. Thus, registering a bean with a name that already exists in the `BeanFactory` will overwrite the previously initialized bean.

# Example Implementation #

Creating a `BeanFactoryPostProcessor` is simply a matter of implementing the interface and registering it with the `InfinitumContext`. The [Component](Component.md) annotation will allow the post processor to be picked up and registered automatically by the framework. The below example shows how a `BeanFactoryPostProcessor` might be implemented to accomplish build-specific configurations.

```
@Component
public class BuildTargetedPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(BeanFactory beanFactory) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            // Configure beans for newer APIs
        } else {
	    // Configure beans for older APIs
        }
    }

}
```

If we are not using component scanning, we can register this post processor in the InfinitumCfgXml:

```
<bean id="buildTargetedPostProcessor"
    src="com.example.postprocessor.BuildTargetedPostProcessor" />
```