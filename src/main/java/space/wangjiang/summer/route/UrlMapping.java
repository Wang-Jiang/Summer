package space.wangjiang.summer.route;

import java.lang.annotation.*;

/**
 * Created by WangJiang on 2017/9/9.
 * <pre>
 * 这个是用于路径参数URL设计的，类似/user/{userId}/blog/{blogId}
 * 暂时只能注解到方法中，如果类也支持，可能会因使用不当导致request的数据被污染
 *
 * 这个是对JFinal的路由设计的一个扩展，在JFinal中这种URL本身是不支持的
 *
 * 其实这种设计在很多框架中都是支持的，比如Django、Spring
 * 不过Django中的是写正则规则匹配，说实话，这个有点多余
 * 我觉得像Spring那种对于绝大多数人来说就够用了，正则还是相对复杂了
 *
 * 在这里，采用和Spring相似的解决方案，@UrlMapping(url="/user/{userId}/view")
 *
 * 但是，我并不准备像Spring那样，在方法参数中写上@PathVariable("userId") String userId
 * 我觉得这种设计并不友好，方法参数加上注解显得很臃肿
 * 我直接将URL中的userId获取，增加到request中
 * Controller中可以像普通的参数一样获取userId的值
 *
 * 其实一开始我还想在这个注解上加上method属性的，效果类似于 @UrlMapping(url="/user/{userId}/edit", method=GET)
 * 这个主要是解决一个问题，比如编辑用户信息，需要一个URL去访问编辑页面，还需要一个URL去提交数据保存
 * 理想情况是GET /user/36/edit表示访问编辑页面，POST /user/36/edit则是提交数据保存，有点RESTful的感觉
 *
 * 目前的URL暂时不支持这么操作，暂时路由表直接URL构建的，而不是METHOD + URL
 *
 * 当然，如果原来的request中就有userId，URL上userId就会将它覆盖，这个是需要注意的
 * 或许以后设计的时候，可以当存在userId的时候，打印一个警告
 *
 * 。。。实现的时候，发现一个巨大的问题，不能修改request参数，没有setParameter，应该是避免参数污染
 * 不能放到parameter里面，只能放到Attribute里面
 * 但是说实话，我觉得还是前者好，毕竟是URL的参数，属性这个有点误导人
 *
 * 为了在使用上更容易理解，增加了一个getPathParam()的方法。当然本质还是调用getAttribute()
 * </pre>
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UrlMapping {

//    public enum METHOD {GET, POST, DELETE, PUT}

    String url();
//    METHOD method();

}

