package com.example.util;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * Created by hxh on 2017/7/5.
 */
public class ProcessorUtils {
    private static ProcessorUtils INSTANCE = null;

    private Elements mEleUtils;
    private Filer mFiler;
    private Messager mMessager;



    private ProcessorUtils(ProcessingEnvironment processingEnv)
    {
        this.mFiler = processingEnv.getFiler();
        this.mMessager = processingEnv.getMessager();
        this.mEleUtils = processingEnv.getElementUtils();
    }


    public static ProcessorUtils getINSTANCE(ProcessingEnvironment processingEnv)
    {
        if(null == INSTANCE)
        {
            synchronized (ProcessorUtils.class)
            {
                if(null == INSTANCE)
                {
                    INSTANCE = new ProcessorUtils(processingEnv);
                }
            }
        }
        return INSTANCE;
    }

    public static ProcessorUtils getINSTANCE()
    {
        return INSTANCE;
    }



    public void info(String msg)
    {
        mMessager.printMessage(Diagnostic.Kind.WARNING,String.format(msg,""));
    }

    public Elements getmEleUtils() {

        return mEleUtils;
    }

    public Filer getmFiler() {
        return mFiler;
    }

    public Messager getmMessager() {
        return mMessager;
    }
}
