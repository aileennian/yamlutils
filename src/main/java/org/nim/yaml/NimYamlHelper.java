package org.nim.yaml;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nim.yaml.spring.annotation.ConfigurationProperties;
import org.nim.yaml.spring.annotation.PropertySource;
import org.nim.yaml.spring.annotation.Value;
import org.nim.yaml.spring.core.io.ClassPathResource;
import org.nim.yaml.spring.core.io.DefaultResourceLoader;
import org.nim.yaml.spring.core.io.Resource;
import org.nim.yaml.spring.util.Assert;
import org.nim.yaml.spring.util.ReflectionUtils;
import org.nim.yaml.spring.util.StringUtils;
import org.yaml.snakeyaml.Yaml;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 */
public class NimYamlHelper<T> {
    private static final Log log = LogFactory.getLog(NimYamlHelper.class);
    public static final String GENERATED_BEAN_NAME_SEPARATOR = "#";

	/**
     * 根据tClass上的注解PropertySource得到配置信息
     * @param tClass
     * @return
     * @throws Exception
     */
    public static <T> T getMap(Class<T> tClass) throws Exception{
        PropertySource propertySource = tClass.getAnnotation(PropertySource.class);
        Assert.notNull(propertySource,tClass.getName()+"@propertySource is not null");
        Assert.notNull(propertySource.value(),tClass.getName()+"@propertySource.value is not null");
        Assert.isTrue(propertySource.value().length>0,tClass.getName()+"@propertySource.value is not null");


        List<Resource> resourceList = new ArrayList<Resource>();
        String[] values = propertySource.value();

        for (String value:values){
            resourceList.add(new ClassPathResource(value));
        }

        Method[] methods = ReflectionUtils.getAllDeclaredMethods(tClass);
        for(Method method:methods) {
            propertySource = method.getAnnotation(PropertySource.class);

            if (propertySource != null && propertySource.value()!=null && propertySource.value().length>0) {
                values = propertySource.value();
                for (String value:values){
                    resourceList.add(new ClassPathResource(value));
                }
            }
        }

        Map<String, Object> map = getMap(resourceList.toArray(new Resource[0]));
        T t = tClass.newInstance();
       return parseToObject(map, t);
    }

    public static MutablePropertySources  getMutableMapPropertySources(Resource... resources) throws Exception{
        final  MutablePropertySources mutablePropertySources = new MutablePropertySources();

        for(Resource resource:resources) {
            Map<String, Object> map = getMap(resource);
            String name = resource.getDescription();
            MapPropertySource mapPropertySource = new MapPropertySource(name,map);
            mutablePropertySources.addLast(mapPropertySource);
        }

        return mutablePropertySources;

    }


    public static Map<String, Object> getMap(Resource... resources) throws Exception{
        YamlMapFactoryBean yamlMapFactoryBean = new YamlMapFactoryBean(resources);
        yamlMapFactoryBean.afterPropertiesSet();
        return yamlMapFactoryBean.getObject();

    }


    public static Properties getPropertes(Resource... resources) throws Exception{
        YamlPropertiesFactoryBean yamlPropertiesFactoryBean = new YamlPropertiesFactoryBean(resources);
        yamlPropertiesFactoryBean.afterPropertiesSet();
        return yamlPropertiesFactoryBean.getObject();

    }




    public static Map<String, Object> getYaml(String location) throws IOException {
        Yaml yaml = new Yaml();

        DefaultResourceLoader loader = new DefaultResourceLoader();
        Resource resource = loader.getResource(location);
        Map<String, Object> ret = (LinkedHashMap<String, Object>) yaml.load(resource.getInputStream());
        
        return ret;

    }


    public static <T> T getYaml(String location, T t) throws IOException {
        Map<String, Object> yamlProperties = NimYamlHelper.getYaml(location);
        return parseToObject(yamlProperties,t);

    }


    public static <T> T parseToObject(Map<String, Object> yamlProperties, T t)  throws RuntimeException{
        Class<T> tClass = (Class<T>) t.getClass();

        ConfigurationProperties configurationProperties= tClass.getDeclaredAnnotation(ConfigurationProperties.class);
        String prefix = configurationProperties.prefix();
        Field[] fs = tClass.getDeclaredFields();
        //获取PrivateClass所有属性

        try {
            //T t = (T) tClass.getClass().newInstance();
            //创建一个实例
            for (int i = 0; i < fs.length; i++) {
                Map<String,T> property = getPropertieName(fs[i]);
                String propertyName = (String) property.keySet().toArray()[0];
               //todo:驼峰转换暂不管
                if (!StringUtils.isEmpty(prefix)) {
                    propertyName = prefix + "." + propertyName;
                }

                Object value = yamlProperties.get(propertyName);
                invokeFieldSetter(t,fs[i],value,property.get(propertyName));

            }

            return t;

        }catch (IllegalAccessException
                | NoSuchMethodException
                | InvocationTargetException
                | ParseException e){
           // log.error(new Throwable(e));
            log.error(e);
            //log.error(e.getMessage(),e);
            throw new RuntimeException(e);

        }

    }




	/**
     *
     * @param field
     * @return k属性名，value：默认值
     */
    public static <T> Map<String,T> getPropertieName(Field field) throws ParseException {
        Annotation[] annotation = field.getAnnotations();

        List<Annotation> ttAnnotationList = new ArrayList<Annotation>(Arrays.asList(annotation));
        Map<String,T> result = new HashMap<String ,T>();
        if (ttAnnotationList.contains(Value.class)){
            String valueValue = field.getAnnotation(Value.class).value();
            String property = valueValue.substring(2, valueValue.lastIndexOf("}"));
            String[] v = property.split(":");

            if (StringUtils.isEmpty(valueValue) || v.length==0){
                throw  new RuntimeException(valueValue+"格式错误！");
            }

            if (v.length>1 && !StringUtils.isEmpty(v[1])){
                String defaultValue=v[1].replace(" ","");
                result.put(v[0],parseValue(field,defaultValue));
            }else{
                result.put(v[0],null);
            }
        }else{
            result.put(field.getName(),null);
        }

       return result;

    }


    


	/**
     *
     * @param value
     * @return k属性名，value：默认值
     */
    public static <T> Map<String,T> getPropertieName(Value value, Class<T> tClass){
        Map<String,T> result = new HashMap<String ,T>();
        String valueValue = value.value();
        String property = valueValue.substring(2, valueValue.lastIndexOf("}"));
        String[] v = property.split(":");

        if (StringUtils.isEmpty(valueValue) || v.length==0){
            throw  new RuntimeException(valueValue+"格式错误！");
        }

        if (v.length>1 && !StringUtils.isEmpty(v[1])){
            String defaultValue=v[1].replace(" ","");
            result.put(v[0], (T) defaultValue);

        }else{
            result.put(v[0],null);
        }
        return result;

    }


    private static <T> T parseValue(Field field, Object oriValue) throws ParseException {
        String fieldType = field.getGenericType().toString();


        // 如果type是类类型，则前面包含"class "，后面跟类名
        if (fieldType.equals("class java.lang.String")
              || fieldType.equals("string") ) {
            return (T) oriValue.toString().trim();

        }


        if (fieldType.equals("class java.lang.Integer")
            || fieldType.equals("int")
            ) {
            return (T) Integer.valueOf(oriValue.toString());

        }



        if (fieldType.equals("class java.lang.Boolean")
            || fieldType.equals("boolean")
            ) {
            String value = oriValue.toString();

            if (value.equalsIgnoreCase("true")
                    || value.equalsIgnoreCase("on")
                    || value.equalsIgnoreCase("yes")) {
                return (T) Boolean.TRUE;

            }else{
                return (T) Boolean.FALSE;
            }
        }


        if (fieldType.equals("class java.util.Date")) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            return (T) simpleDateFormat.parse(oriValue.toString());
        }

        return (T) oriValue;

    }



    


	/**
     *  给属性赋值
     * @param t
     * @param field
     * @param value
     * @param defaultValue
     * @param <T>
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws ParseException
     */
    public static <T> void invokeFieldSetter(T t, Field field,Object value, Object defaultValue) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ParseException {
        Class<T> tClass = (Class<T>) t.getClass();

        if ((defaultValue==null || StringUtils.isEmpty(defaultValue.toString()))
                && field.get(t)!=null){
            defaultValue = field.get(t);
        }


        String fieldName = field.getName();
        String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        Method method = null;
        String fieldType = field.getGenericType().toString();
        if (fieldType.equals("class java.lang.String")) { // 如果type是类类型，则前面包含"class "，后面跟类名
            method = tClass.getMethod(methodName, String.class);

            method.invoke(t, value.toString().trim());

        }


        if (fieldType.equals("class java.lang.Integer")) {
            if (value == "") value = 0;

            method = tClass.getMethod(methodName, Integer.class);

            method.invoke(t, Integer.valueOf(value.toString()));

        }

        if (fieldType.equals("int")) { // 如果type是类类型，则前面包含"class "，后面跟类名
            method = tClass.getMethod(methodName, int.class);

            method.invoke(t, Integer.parseInt(value.toString()));

        }

        if (fieldType.equals("class java.lang.Boolean")) {
            method = tClass.getMethod(methodName, Boolean.class);

            if (value.toString().equalsIgnoreCase("true") ||
                    value.toString().equalsIgnoreCase( "on")
            ) {
                method.invoke(t, Boolean.TRUE);

            } else {
                method.invoke(t, Boolean.FALSE);

            }

        }

        if (fieldType.equals("boolean")) { // 如果type是类类型，则前面包含"class "，后面跟类名
            method = tClass.getMethod(methodName, boolean.class);

            if (value.toString().equalsIgnoreCase( "true") ||
                    value.toString().equalsIgnoreCase("on")
            ) {
                method.invoke(t, true);

            } else {
                method.invoke(t, false);

            }

        }

        if (fieldType.equals("class java.util.Date")) {
            method = tClass.getMethod(methodName, Date.class);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

            method.invoke(t, simpleDateFormat.parse(value.toString()));

        } else {
            method = tClass.getMethod(methodName, Object.class);

            method.invoke(t, value);

        }

    }


//    public static String getPropertySourceName(PropertySource propertySource,Class<?> tClass) {
//        Assert.notNull(propertySource, propertySource + " is not null");

//        Assert.notNull(propertySource.value(), propertySource + ".value is not null");

//        Assert.isTrue(propertySource.value().length > 0, propertySource + ".value is not null");

//
//        if (StrUtil.isNotBlank(propertySource.name())) {
//            return tClass.getName();

//        } else {
//            return propertySource.name();

//        }

//    }

}

