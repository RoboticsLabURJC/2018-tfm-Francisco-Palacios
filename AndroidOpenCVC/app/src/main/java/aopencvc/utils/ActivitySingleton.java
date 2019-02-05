package aopencvc.utils;

import android.app.Activity;

public class ActivitySingleton {

    public static ActivitySingleton singletonObject;

    private static Activity baseActivity;


    public ActivitySingleton()
    {
        //   Optional Code
    }
    public static synchronized ActivitySingleton getSingletonObject()
    {
        if (singletonObject == null)
        {
            singletonObject = new ActivitySingleton();
        }
        return singletonObject;
    }


    /**
     * used to clear CommonModelClass(SingletonClass) Memory
     */
    public void clear()
    {
        singletonObject = null;
    }


    public Object clone() throws CloneNotSupportedException
    {
        throw new CloneNotSupportedException();
    }

    //getters and setters starts from here.it is used to set and get a value

    public Activity getbaseActivity()
    {
        return baseActivity;
    }

    public void setbaseActivity(Activity baseActivity)
    {
        this.baseActivity = baseActivity;
    }
}
