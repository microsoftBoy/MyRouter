package com.example.shuaige.myrouter.compiler.processor;

import com.example.shuaige.myrouter.compiler.utils.Logger;
import com.example.shuaige.myrouter.compiler.utils.TypeUtils;
import com.example.shuaige.myrouter.facade.annotation.Route;
import com.example.shuaige.myrouter.facade.enums.RouteType;
import com.example.shuaige.myrouter.facade.model.RouteMeta;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.Writer;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.StandardLocation;

import static com.example.shuaige.myrouter.compiler.utils.Consts.ACTIVITY;
import static com.example.shuaige.myrouter.compiler.utils.Consts.ANNOTATION_TYPE_ROUTE;
import static com.example.shuaige.myrouter.compiler.utils.Consts.ANNOTATION_TYPE_ROUTETEST;
import static com.example.shuaige.myrouter.compiler.utils.Consts.IROUTE_GROUP;
import static com.example.shuaige.myrouter.compiler.utils.Consts.IROUTE_ROOT;
import static com.example.shuaige.myrouter.compiler.utils.Consts.KEY_GENERATE_DOC_NAME;
import static com.example.shuaige.myrouter.compiler.utils.Consts.KEY_MODULE_NAME;
import static com.example.shuaige.myrouter.compiler.utils.Consts.METHOD_LOAD_INTO;
import static com.example.shuaige.myrouter.compiler.utils.Consts.NAME_OF_GROUP;
import static com.example.shuaige.myrouter.compiler.utils.Consts.NAME_OF_ROOT;
import static com.example.shuaige.myrouter.compiler.utils.Consts.PACKAGE_OF_GENERATE_DOCS;
import static com.example.shuaige.myrouter.compiler.utils.Consts.PACKAGE_OF_GENERATE_FILE;
import static com.example.shuaige.myrouter.compiler.utils.Consts.SEPARATOR;
import static com.example.shuaige.myrouter.compiler.utils.Consts.VALUE_ENABLE;
import static com.example.shuaige.myrouter.compiler.utils.Consts.WARNING_TIPS;

/**
 * Created by ShuaiGe on 2018-08-17.
 */

@AutoService(Processor.class)
//处理器接收的参数，module名字
@SupportedOptions(KEY_MODULE_NAME)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
//要过滤的注解类
@SupportedAnnotationTypes({ANNOTATION_TYPE_ROUTE,ANNOTATION_TYPE_ROUTETEST})
public class RouteProcessor extends AbstractProcessor {

    private static final Object TAG = "RouteProcessor";
    private Map<String, String> rootMap = new TreeMap<>();//分组映射表
    private Map<String, Set<RouteMeta>> groupMap = new HashMap<>();//组内路由映射表
    private Filer mFiler;
    private Types types;
    private Elements elements;
    private TypeUtils typeUtils;
    private String moduleName;
    private boolean generateDoc;
    private Logger logger;
    private Writer docWriter;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        logger = new Logger(processingEnvironment.getMessager());
        logger.info(">>> RouteProcessor init start. <<<");
        mFiler = processingEnvironment.getFiler(); //文件生成器，用于生成class文件
        types = processingEnvironment.getTypeUtils();//type（类信息） 工具类
        elements = processingEnvironment.getElementUtils();//class文件元素（类，属性，方法）工具类

        typeUtils = new TypeUtils(types, elements);//获取真正的Java类型，生成javaDoc 用


        //获取module名称
        Map<String, String> options = processingEnvironment.getOptions();
        if (MapUtils.isNotEmpty(options)) {
            moduleName = options.get(KEY_MODULE_NAME);
            generateDoc = VALUE_ENABLE.equals(options.get(KEY_GENERATE_DOC_NAME));
        }

        if (StringUtils.isNotEmpty(moduleName)) {
            moduleName = moduleName.replaceAll("[^0-9a-zA-Z_]+", "");
            logger.info("The user has configuration the module name, it was [" + moduleName + "]");
        } else {
            logger.error("These no module name, at 'build.gradle', like :\n" +
                    "android {\n" +
                    "    defaultConfig {\n" +
                    "        ...\n" +
                    "        javaCompileOptions {\n" +
                    "            annotationProcessorOptions {\n" +
                    "                arguments = [AROUTER_MODULE_NAME: project.getName()]\n" +
                    "            }\n" +
                    "        }\n" +
                    "    }\n" +
                    "}\n");
            throw new RuntimeException("ARouter::Compiler >>> No module name, for more information, look at gradle log.");
        }

        if (generateDoc) {
            try {
                docWriter = mFiler.createResource(StandardLocation.SOURCE_OUTPUT, PACKAGE_OF_GENERATE_DOCS,
                        "myrouter-map-of-" + moduleName + ".json").openWriter();
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("Creat doc writer failed because " + e.getMessage());
            }
        }

        logger.info(">>> RouteProcessor init end. <<<");
    }

    /**
     *
     * @param set 包含含有注解字段的注解类信息，如我们使用到的Route类(对应SupportedAnnotationTypes中过滤的注解类)
     * @param roundEnvironment 包含含有注解字段的原始类信息，如添加了Route注解字段Activity的类
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        logger.info(">>> RouteProcessor process start. <<<");
        logger.info(">>> RouteProcessor process set "+set.toString());
        logger.info(">>> RouteProcessor process roundEnvironment "+roundEnvironment.toString());
        //set不为空，说明包含有注解字段，
        if (CollectionUtils.isNotEmpty(set)) {
            Set<? extends Element> elementsAnnotatedWith = roundEnvironment
                    .getElementsAnnotatedWith(Route.class);
            try {
                logger.info(">>> Found routes, start... <<<");
                parseRoutes(elementsAnnotatedWith);
            } catch (IOException e) {
                logger.error(e);
            }
            return true;
        }
        return false;
    }

    /**
     * 解析路由节点
     *
     * @param routeElements
     */
    private void parseRoutes(Set<? extends Element> routeElements) throws IOException {
        if (CollectionUtils.isNotEmpty(routeElements)) {

            logger.info(">>> parseRoutes routeElements size = " + routeElements.size());

            rootMap.clear();

            TypeMirror type_Activity = elements.getTypeElement(ACTIVITY).asType();

            //router 的接口
            TypeElement type_IRouteGroup = elements.getTypeElement(IROUTE_GROUP);


            ClassName routeMetaCn = ClassName.get(RouteMeta.class);
            ClassName routeTypeCn = ClassName.get(RouteType.class);

            logger.info(">>> parseRoutes routeElements routeMetaCn = " + routeMetaCn.toString());


            /**
             * 构造输入类型，比如：
             * ```Map<String, Class<? extends IRouteGroup>>```
             */

            ParameterizedTypeName inputMapTypeOfRoot = ParameterizedTypeName.get(ClassName.get(Map.class),
                    ClassName.get(String.class),
                    ParameterizedTypeName.get(ClassName.get(Class.class),
                            WildcardTypeName.subtypeOf(ClassName.get(type_IRouteGroup))));

            /**
             * Map<String, RouteMeta>
             */
            ParameterizedTypeName inputMapTypeOfGroup = ParameterizedTypeName.get(ClassName.get(Map.class),
                    ClassName.get(String.class),
                    ClassName.get(RouteMeta.class));

            /**
             * 构建输入参数
             */
            ParameterSpec rootParameterSpec = ParameterSpec.builder(inputMapTypeOfRoot, "routes").build();
            ParameterSpec groupParameterSpec = ParameterSpec.builder(inputMapTypeOfGroup, "atlas").build();

            /**
             * 构造Root的‘loadinto’方法
             */
            MethodSpec.Builder loadIntoMethodOfRootBuilder = MethodSpec.methodBuilder(METHOD_LOAD_INTO)
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(rootParameterSpec);

            for (Element element : routeElements) {
                logger.info(">>> Found activity element: " + element.toString() + " <<<");
                TypeMirror tm = element.asType();
                Route route = element.getAnnotation(Route.class);

                logger.info(">>> Found activity route path: " + route.path() + " <<<");
                logger.info(">>> Found activity route group: " + route.group() + " <<<");
                RouteMeta routeMeta = null;
                //如果使用了Route的类是activity类型的
                if (types.isSubtype(tm, type_Activity)) {
                    logger.info(">>> Found activity TypeMirror: " + tm.toString() + " <<<");

                    logger.info(">>> Found activity route: " + route.toString() + " <<<");

                    routeMeta = new RouteMeta(route, RouteType.ACTIVITY, element, null);

                    logger.info(">>> Found activity routeMeta: " + routeMeta.toString() + " <<<");
                }

                categories(routeMeta);
            }

            for (Map.Entry<String, Set<RouteMeta>> entry : groupMap.entrySet()) {
                String groupName = entry.getKey();
                Set<RouteMeta> groupData = entry.getValue();

                //构造Group的"loadinto"方法
                MethodSpec.Builder loadIntoMethodOfGroupBuilder = MethodSpec.methodBuilder(METHOD_LOAD_INTO)
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(groupParameterSpec);

                for (RouteMeta routeMeta : groupData) {
                    TypeElement typeElement = (TypeElement) routeMeta.getRawType();
                    ClassName className = ClassName.get(typeElement);

                    //构造参数
                    StringBuilder mapBodyBuilder = new StringBuilder();
                    Map<String, Integer> paramType = routeMeta.getParamType();
                    if (MapUtils.isNotEmpty(paramType)) {
                        for (Map.Entry<String, Integer> types : paramType.entrySet()) {
                            mapBodyBuilder.append("put(\"").append(types.getKey()).append("\",")
                                    .append(types.getValue()).append(");");
                        }
                    }

                    String mapBody = mapBodyBuilder.toString();


                    //例如：atlas.put("/main/home", RouteMeta.build(RouteType.ACTIVITY, HomeActivity.class, "/main/home", "main", null, -1, -2147483648));
                    loadIntoMethodOfGroupBuilder.addStatement(
                            "atlas.put($S,$T.build($T." + routeMeta.getType() + ",$T.class,$S,$S,"
                                    + (StringUtils.isEmpty(mapBody) ? null : ("new java.util.HashMap<String,Integer>(){{" + mapBody + "}}")) + "," + routeMeta.getPriority() + "," + routeMeta.getExtra() + "))",
                            routeMeta.getPath(),
                            routeMetaCn,
                            routeTypeCn,
                            className,
                            routeMeta.getPath().toLowerCase(),
                            routeMeta.getGroup().toLowerCase());

                }

                //生成Group类 代码
                String groupFileName = NAME_OF_GROUP + groupName;
                JavaFile.builder(PACKAGE_OF_GENERATE_FILE,
                        TypeSpec.classBuilder(groupFileName)
                                .addJavadoc(WARNING_TIPS)
                                .addSuperinterface(ClassName.get(type_IRouteGroup))
                                .addMethod(loadIntoMethodOfGroupBuilder.build())
                                .addModifiers(Modifier.PUBLIC).build()).build().writeTo(mFiler);
                logger.info(">>> Generated group: " + groupName + "<<<");
                rootMap.put(groupName, groupFileName);

            }
            logger.info(">>> Generated group end : " + rootMap.size() + "<<<");

            if (MapUtils.isNotEmpty(rootMap)) {
                for (Map.Entry<String, String> entry :
                        rootMap.entrySet()) {
                    //向loadinto方法中添加具体执行的语句
                    loadIntoMethodOfRootBuilder.addStatement("routes.put($S,$T.class)",
                            entry.getKey(),
                            ClassName.get(PACKAGE_OF_GENERATE_FILE, entry.getValue()));

                }
            }

            //生成root类 代码
            String rootFileName = NAME_OF_ROOT + SEPARATOR + moduleName;
            JavaFile.builder(PACKAGE_OF_GENERATE_FILE,
                    TypeSpec.classBuilder(rootFileName)
                            .addJavadoc(WARNING_TIPS)
                            .addSuperinterface(ClassName.get(elements.getTypeElement(IROUTE_ROOT)))
                            .addModifiers(Modifier.PUBLIC)
                            .addMethod(loadIntoMethodOfRootBuilder.build())
                            .build()).build().writeTo(mFiler);

            logger.info(">>> Generated root, name is " + rootFileName + " <<<");
        }

    }


    /**
     * 根据组名，放入对应的映射
     *
     * @param routeMeta
     */
    private void categories(RouteMeta routeMeta) {
        if (routeVerify(routeMeta)) {
            logger.info(">>> start categories,group = " + routeMeta.getGroup() + ", path = " + routeMeta.getPath() + " <<<");
            Set<RouteMeta> routeMetas = groupMap.get(routeMeta.getGroup());
            if (CollectionUtils.isEmpty(routeMetas)) {
                TreeSet<RouteMeta> routeMetaSet = new TreeSet<>(new Comparator<RouteMeta>() {
                    @Override
                    public int compare(RouteMeta routeMeta, RouteMeta t1) {
                        try {
                            //排序
                            return routeMeta.getPath().compareTo(t1.getPath());
                        } catch (NullPointerException e) {
                            logger.error(e.getMessage());
                            return 0;
                        }
                    }
                });
                routeMetaSet.add(routeMeta);
                groupMap.put(routeMeta.getGroup(), routeMetaSet);
            } else {
                routeMetas.add(routeMeta);
            }
        } else {
            logger.warning(">>> Route meta verify error, group is " + routeMeta.getGroup() + " <<<");
        }

        logger.info(">>> end categories ");


    }

    private boolean routeVerify(RouteMeta routeMeta) {
        String path = routeMeta.getPath();
        logger.info(">>> routeVerify  path = " + path);
        //路径必须以“/”开头
        if (StringUtils.isEmpty(path) || !path.startsWith("/")) {
            return false;
        }

        if (StringUtils.isEmpty(routeMeta.getGroup())) {
            try {
                String defaultGroup = path.substring(1, path.indexOf("/", 1));
                if (StringUtils.isEmpty(defaultGroup)) {
                    return false;
                }
                routeMeta.setGroup(defaultGroup);
                return true;
            } catch (Exception e) {
                logger.error("Failed to extract default group! " + e.getMessage());
                return false;
            }
        }
        return true;
    }
}
