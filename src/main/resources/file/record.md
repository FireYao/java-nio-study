> hibernate :  
>
> UserType  自定义类型  
>
> @TypeDefs {@TypeDef  @Parameter}
>
> @Type
>
> 主要实现自定义数据格式转换

> UserType接口中的方法具体作用
>
> [介绍Hibernate使用UserType \- wangshfa的专栏 \- CSDN博客](http://blog.csdn.net/wangshfa/article/details/22687813)

```java
int[] sqlTypes();//修改类型对应的SQL类型,告诉Hibernate要使用什么SQL列类型生成DDL模式

Class returnedClass();//映射Java值类型

boolean equals(Object var1, Object var2) throws HibernateException;//自定义数据类型比对方法

int hashCode(Object var1) throws HibernateException;//返回给定类型的hashCode
/*
	从JDBC的ResultSet获取属性值
	读取数据转换为自定义类型返回,var2包含了自定义类型的映射字段名称
*/
Object nullSafeGet(ResultSet var1, String[] var2, SessionImplementor var3, Object var4) throws HibernateException, SQLException;

void nullSafeSet(PreparedStatement var1, Object var2, int var3, SessionImplementor var4) throws HibernateException, SQLException;//数据保存时被调用,把属性值写到JDBC的PreparedStatement

/*
	1.当nullSafeGet方法调用之后，我们获得了自定义数据对象，在向用户返回自定义数据之前
	 	deepCopy方法被调用，它将根据自定义数据对象构造一个完全拷贝，把拷贝返还给客户使用。
	2.此时我们就得到了自定义数据对象的两个版本, 原始版本由hibernate维护,
	 	用作脏数据检查依据，复制版本由用户使用，hibernate将在脏数据检查过程中比较这两个版本的数据
*/
Object deepCopy(Object var1) throws HibernateException;//创建值的快照

boolean isMutable(); //这个类型是否可变
/*
	以序列化的形保存信息的数据的高速缓存
*/
Serializable disassemble(Object var1) throws HibernateException;

/*
	高速缓存的数据转变为MonetaryAmount的一个实例
*/
Object assemble(Serializable var1, Object var2) throws HibernateException;
/*
 	处理脱管对象状态的合并
*/
Object replace(Object var1, Object var2, Object var3) throws HibernateException;
```
1. 业务逻辑层分为了 servise 和 biz,biz层单独处理一个对象Repository,进行特性数据校验处理等,service层调用biz	

2. BaseQuery中处理所有子查询类的specification查询逻辑,也可以在子类toSpec()中单独处理查询逻辑

   @QueryWord( column="",func="MatchType.like" ) column:字段名,func:where语句条件," like,equal,ge,le,gt,lt,notequal,graterThan......"

   使用@QueryWord注解会自动在BaseQuery中构建Specification查询条件

3. 表单视图: ?  extends BaseForm

4. 列表视图 ? extends BaseListView

5. IBaseBiz 多租户biz基类:  数据隔离(公司/租户) 不同公司/租户的数据必须隔离 //un

6. IMultenantbaseBiz 非多租户biz基类 : 比如说平台(简图旅行)/目的地公司,巴士,产品等,数据共享性. //un

7. AbstractDomain 所有值对象的基类,定义了一个泛型自增长主键

8. OptionUtil  针对枚举类型的下拉列表的工具类

9. @App,@Mod,@Nod注解来添加权限功能,系统会在启动时去扫描这些注解然后读取数据将功能添加到redis中.

   ```java
   public class PubAppHandler implements ApplicationContextAware {
     @Override
       public void setApplicationContext(ApplicationContext applicationContext) throws 		BeansException {
           //扫描注解,读取配置信息
         	//将所有信息存redis
       }
   }
   // 会在spring启动前执行 setApplicationContext方法
   //这里会去扫描所有Controller包下@App,@Mod,@Nod注解.
   ```

10. ```java
  /**
  	@RequestMapping(value = {"", "/home"}, method = RequestMethod.GET)
      public String home(@EntryPoint(scope = Scope.PLATFORM) Set<PubApp> entry, Model model)
      */
   //WebArgumentResolver里扫描@EntryPoiot注解,读取当前登陆用户的角色权限entry,然后通过参数注入方
   //法里.
  ```

11. ```java

   public class LoginForm {

       @NotBlank(message = "用户名不能为空")
       String userName;

       @NotBlank(message = "密码不能为空")
       @Length(min = 6, max = 64, message = "密码长度为6-64")
       String password;
   }

   /*
   	@NotNull //校验基本数据类上,不为null
   	@NotEmpty //校验集合,数组,字符串,不能为null且长度不为0
   	@NotBlank //校验String,尾部空格会被忽略.
   	@Valid 以上注解和valid配合使用才会起作用.
   */
    @ResponseBody
       @RequestMapping(value = "login", method = RequestMethod.POST)
       public BaseDataResponse login(@RequestBody @Valid LoginForm loginForm, BindingResult bindingResult) {
           //..自动校验LoginForm的参数
       }
   ```

12. checkPermission

   ```java
   //注册自定义的PubAuthorizationAttributeSourceAdvisor
   //注入SecurityManager
   private static final Class<? extends Annotation>[] AUTHZ_ANNOTATION_CLASSES =
               new Class[] {
                       Node.class,
               };
   //添加权限认证需要的注解. @Node注解的方法必须进行权限认证
   //重写public boolean matches(Method method, Class<?> targetClass)
   //重写不需要改动复用父类的实现就可以
   public PubAuthorizationAttributeSourceAdvisor() {
           setAdvice(new PubAnnotationsAuthorizingMethodInterceptor());
     		//需要set一个Aop,这个切面会将权限认证的实现类加载到shiro拦截器栈中.
       }
   ```

   ```java
   public class PubAnnotationsAuthorizingMethodInterceptor extends AopAllianceAnnotationsAuthorizingMethodInterceptor
           implements MethodInterceptor{

       public PubAnnotationsAuthorizingMethodInterceptor() {
           List<AuthorizingAnnotationMethodInterceptor> interceptors = new ArrayList<>(1);
           AnnotationResolver resolver = new SpringAnnotationResolver();

           interceptors.add(new PubPermissionAnnotationMethodInterceptor(resolver));
   		//这里将自定义的权限认证AOP添加到拦截器栈中
           setMethodInterceptors(interceptors);
       }
   }
   ```

   ```java
   public class PubPermissionAnnotationMethodInterceptor extends AuthorizingAnnotationMethodInterceptor{
       @Override
       public void assertAuthorized(MethodInvocation mi) throws AuthorizationException {
           //...在这里 会拦截Controller请求,
         Class targetClass = mi.getThis().getClass();//请求的Controller类
         //先根据targetClass找到类上的注解@Mod---(Mod)targetClass.getAnnotation(Mod.class);
         //再找到包上的注解@App----在每个包的package-info.java中
         //targetClass.getPackage().getAnnotation(App.class);
         //再找到方法上的注解@Node---mi.getMethod().getAnnotation(Node.class);
         // 然后根据targetClass找到Node,Mod,App注解,然后在把App.code|Mod.code|Node.code拼接
         SecurityUtils.getSubject().checkPermission(permission);
         //最后在检查当前用户是否有该请求所需的权限.
       }
   }
   ```

   ​