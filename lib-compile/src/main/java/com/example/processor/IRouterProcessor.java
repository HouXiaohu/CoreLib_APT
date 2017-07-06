package com.example.processor;

import com.example.AnnotationProcessor;
import com.example.IProcessor;

import com.example.annotation.XRouter;
import com.example.util.ProcessorUtils;
import com.example.util.TypeUtil;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;

/**
 *@Title   Router 注解处理器，没有提供对外Api，
 * 用法： 使用Router注解后，进行make project ，使之动态生成XRouter代码，
 * 之后敲出XRouter，如果可见，代表生成成功，不可见，请 clean -> make project
 *@Time 17/7/5 19:52  版本0.1  后续更新对外Api
 */
public class IRouterProcessor implements IProcessor
{

    @Override
    public void process(RoundEnvironment roundEnv, AnnotationProcessor processor) {
        String cn = "XRouter";
        TypeSpec.Builder classBuilder =TypeSpec.classBuilder(cn)
                .addModifiers(Modifier.PUBLIC,Modifier.FINAL);


        FieldSpec extraField = FieldSpec.builder(ParameterizedTypeName.get(HashMap.class,String.class,Object.class),"mCurActivityExtra")
                .addModifiers(Modifier.PUBLIC,Modifier.STATIC)
                .build();

        FieldSpec context = FieldSpec.builder(TypeUtil.ANDROID_CONTEXT,"mContext")
                .addModifiers(Modifier.PUBLIC,Modifier.STATIC)
                .build();

        classBuilder.addField(extraField);
        classBuilder.addField(context);

        MethodSpec.Builder methodMain = MethodSpec.methodBuilder("go")
                .addModifiers(Modifier.PUBLIC,Modifier.STATIC)
                .addParameter(String.class,"name")
                .addParameter(ParameterizedTypeName.get(HashMap.class,String.class,Object.class),"extra")
                .addParameter(TypeUtil.ANDROID_VIEW,"view")
                ;

        MethodSpec methodBuider2 = MethodSpec.methodBuilder("go")
                .addModifiers(Modifier.PUBLIC,Modifier.STATIC)
                .addParameter(String.class,"name")
                .addCode("go(name,null,null);\n").build();

        MethodSpec methodBuider3 = MethodSpec.methodBuilder("go")
                .addModifiers(Modifier.PUBLIC,Modifier.STATIC)
                .addParameter(String.class,"name")
                .addParameter(ParameterizedTypeName.get(HashMap.class,String.class,Object.class),"extra")
                .addCode("go(name,extra,null);\n").build();


        MethodSpec method_init = MethodSpec.methodBuilder("init")
                .addParameter(TypeUtil.ANDROID_CONTEXT,"context")
                .addModifiers(Modifier.PUBLIC,Modifier.STATIC)
                .addStatement("mContext = context")
                .build();

//        MethodSpec methodBind = MethodSpec.methodBuilder("bind")
//                .addModifiers(Modifier.PUBLIC,Modifier.STATIC)
//                .addParameter(TypeUtil.ANDROID_ACTIVITY,"mContext").build();


        List<ClassName> mList = new ArrayList<>();
        methodMain.addStatement("mCurActivityExtra=extra")
//                .addStatement("$T mContext = ($T)view.getContext()",TypeUtil.ANDROID_ACTIVITY,TypeUtil.ANDROID_ACTIVITY)
        ;



        CodeBlock.Builder codeBuilder = CodeBlock.builder();

        codeBuilder.add("if(null != view)mContext = view.getContext();");

        codeBuilder.add("$T intent = new $T();\n",TypeUtil.ANDROID_INTENT,TypeUtil.ANDROID_INTENT);
        codeBuilder.add("switch(name)\n{\n");

        List<RouterModel> mRouterActivityModels = new ArrayList<>();
        try
        {
            for (TypeElement typeElement : ElementFilter.typesIn(roundEnv.getElementsAnnotatedWith(XRouter.class))) {
                ClassName currenttype = ClassName.get(typeElement);
                if(mList.contains(currenttype)) continue;

                mList.add(currenttype);

                RouterModel mondel = new RouterModel();
                mondel.setElement(typeElement);
                mondel.setActionName(typeElement.getAnnotation(XRouter.class).value());

//                List<Element> mExtraElemetns = new ArrayList<>();
//                List<String> mExtraElementnKeys = new ArrayList<>();

                //得到这个类中的所有元素
//                for (Element childitem : typeElement.getEnclosedElements()) {
//                    Extra mExtraAnnotation =  childitem.getAnnotation(Extra.class);
//                    if(null != mExtraAnnotation)
//                    {
//                        mExtraElementnKeys.add(mExtraAnnotation.value());
//                        mExtraElemetns.add(childitem);
//                    }
//                }
//
//                mondel.setExtraElementKeys(mExtraElementnKeys);
//                mondel.setExtraElements(mExtraElemetns);
                mRouterActivityModels.add(mondel);
            }



            for (RouterModel item : mRouterActivityModels) {
                codeBuilder.add("case $S:\n",item.getActionName());
                codeBuilder.add("intent.setClass(mContext,$L.class);\n",item.getElement());
                //methodMain.addStatement("mContext.startActivity(new Intent(mContext,$L.class))");
                codeBuilder.add("break;\n");
            }
            codeBuilder.add("}\n");

            setIntentParam(codeBuilder);

            codeBuilder.add("  if (!(mContext instanceof $T))intent.addFlags($T.FLAG_ACTIVITY_NEW_TASK);",TypeUtil.ANDROID_ACTIVITY,TypeUtil.ANDROID_INTENT);
            codeBuilder.add("mContext.startActivity(intent);\n");

            methodMain.addCode(codeBuilder.build());

            classBuilder.addMethod(method_init);

            classBuilder.addMethod(methodMain.build());
            classBuilder.addMethod(methodBuider2);
            classBuilder.addMethod(methodBuider3);


            JavaFile javafile = JavaFile.builder("com.hxh.apt",classBuilder.build()).build();
            javafile.writeTo(ProcessorUtils.getINSTANCE().getmFiler());
        }catch (Exception e)
        {

        }

    }

    private void setIntentParam( CodeBlock.Builder build)
    {
        build.add("if(null !=mCurActivityExtra &&  mCurActivityExtra.keySet().size() >0)\n{\n");
        build.add("for($T s : mCurActivityExtra.keySet())\n{\n",String.class);
        build.add("$T value = mCurActivityExtra.get(s);\n", TypeName.OBJECT);
        build.add(" if (value instanceof $T)\n{\n",String.class);
        build.add(" intent.putExtra(s, ((String) mCurActivityExtra.get(s)));\n}\n");

        build.add("else if (value instanceof $T)\n{\n",Integer.class);
        build.add(" intent.putExtra(s, ((Integer) mCurActivityExtra.get(s)));\n}\n");

        build.add("else if (value instanceof $T)\n{\n",Float.class);
        build.add(" intent.putExtra(s, ((Float) mCurActivityExtra.get(s)));\n}\n");

        build.add(" else if (value instanceof $T)\n{\n",Double.class);
        build.add(" intent.putExtra(s, ((Double) mCurActivityExtra.get(s)));\n}\n");

        //Parceable 暂不支持
//        build.addStatement(" if (value instanceof $T){",Parcelable.class);
//        build.addStatement(" intent.putExtra(s, ((String) param.get(s)))\n}\n");

        build.add("else if (value instanceof $T)\n{\n",Serializable.class);
        build.add(" intent.putExtra(s, ((Serializable) mCurActivityExtra.get(s)));\n}\n");
        build.add("}}");

    }

}
