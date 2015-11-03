**Contents**


# Introduction #

In order to support communication with web services which require some sort of request authentication, Infinitum provides the `AuthenticationStrategy`. `AuthenticationStrategy` describes how web service requests are authenticated. This interface should be implemented for specific web service authentication strategies. If a web service uses token or shared-secret authentication, SharedSecretAuthentication should be used.

`AuthenticationStrategy` has a handful of methods which must be implemented. One such method, `getAuthenticationString()`, returns the string used to authenticate web service requests and is typically appended to the request URI or included as a header. Another method, `isHeader()` indicates just that, whether authentication is included in the request query string or in a header (authentication is done via query string by default).

# Specifying an `AuthenticationStrategy` #

An `AuthenticationStrategy` can be specified for a [RestfulSession](http://code.google.com/p/infinitum-framework/wiki/Session#RestfulSession) in InfinitumCfgXml using one of two ways. One of Infinitum's built-in strategies, such as `SharedSecretAuthentication`, can be specified using the following:

```
<rest>
    <property name="host">http://localhost/mywebservice</property>
    <authentication strategy="token" header="true" enabled="true">
        <property name="tokenName">token</property>
        <property name="token">e489e8383c0ae2b7fe4dcf178330b4ac</property>
    </authentication>
</rest>
```

This indicates to the framework that `SharedSecretAuthentication` should be used with the given token name and token code.

In order to specify an alternate `AuthenticationStrategy` implementation, a bean is referenced.

```
<rest>
    <property name="host">http://localhost/mywebservice</property>
    <authentication ref="myAuthentication" enabled="true" />
</rest>
<beans>
    <bean id="myAuthentication" src="com.example.rest.MyAuthentication" />
</beans>
```