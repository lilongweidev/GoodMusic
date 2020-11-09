package com.llw.goodmusic.livedata;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * liveData管道总线
 *
 * @author llw
 */
public class LiveDataBus {
    /**
     * LiveData集合
     */
    private Map<String, BusMutableLiveData<Object>> liveDataMap;
    private static LiveDataBus liveDataBus = new LiveDataBus();

    private LiveDataBus(){
        liveDataMap = new HashMap<>();
    }

    public static LiveDataBus getInstance(){
        return liveDataBus;
    }


    /**
     * 这个是存和取一体的方法
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public synchronized<T> BusMutableLiveData<T> with(String key,Class<T> clazz){
        if(!liveDataMap.containsKey(key)){
            liveDataMap.put(key,new BusMutableLiveData<Object>());
        }
        return (BusMutableLiveData<T>) liveDataMap.get(key);
    }


    public static class BusMutableLiveData<T> extends MutableLiveData<T> {
        //是否需要粘性事件
        private boolean isHad = false;


        //重写observe的方法
        public void observe(@NonNull LifecycleOwner owner, boolean isHad,@NonNull Observer<? super T> observer) {
           if(isHad){
               this.isHad = true;
           }else{
               this.isHad = false;
           }
            this.observe(owner,observer);
        }

        //重写observe的方法
        @Override
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
            super.observe(owner, observer);
            //改变observer.mLastVersion >= mVersion这个判断  然后拦截onChanged
            try {
                if(this.isHad){
                    hook((Observer<T>) observer);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * hook方法  hook系统源码  改变系统的一些参数
         * @param observer
         */
        private void hook(Observer<T> observer) throws Exception {
            //获取到LiveData的类对象
            Class<LiveData> liveDataClass = LiveData.class;
            //获取到mObservers的反射对象
            Field mObserversField = liveDataClass.getDeclaredField("mObservers");
            //让mObserversField可以被访问
            mObserversField.setAccessible(true);
            //获取到这个mObserversField的值
            Object mObservers = mObserversField.get(this);
            //获取到mObservers的get方法的反射对象
            Method get = mObservers.getClass().getDeclaredMethod("get", Object.class);
            //设置这个反射对象可以被访问
            get.setAccessible(true);
            //执行这个方法 得到Entry
            Object invokeEntry = get.invoke(mObservers, observer);
            //定义一个空的对象  LifecycleBoundObserver
            Object observerWrapper = null;
            if(invokeEntry!=null && invokeEntry instanceof Map.Entry){
                observerWrapper = ((Map.Entry)invokeEntry).getValue();
            }
            if(observerWrapper == null){
                throw new NullPointerException("ObserverWrapper不能为空");
            }
            //获取到ObserverWrapper的类对象
            Class<?> superclass = observerWrapper.getClass().getSuperclass();
            //获取搭配这个类中的mLastVersion成员变量
            Field mLastVersionField = superclass.getDeclaredField("mLastVersion");
            mLastVersionField.setAccessible(true);
            //获取到mVersion的反射对象
            Field mVersionField = liveDataClass.getDeclaredField("mVersion");
            //打开权限
            mVersionField.setAccessible(true);
            //得到的就是mVersion在当前类中的值
            Object o = mVersionField.get(this);
            //把它的值给mLastVersion
            mLastVersionField.set(observerWrapper,o);
        }
    }

}
