package cn.wuxia.project.basic.test;

import cn.wuxia.project.common.third.fenci.BosonUtil;
import cn.wuxia.project.common.third.fenci.bean.NerBean;
import cn.wuxia.common.util.JsonUtil;
import cn.wuxia.common.util.ListUtil;
import cn.wuxia.common.util.reflection.ReflectionUtil;
import com.google.common.collect.Lists;
import jodd.util.StringUtil;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainTest {

   // @Test
    public void name() throws Exception {
        NerBean nerBean = BosonUtil.ner("星期一提醒我吃饭, 后天吃饭, 下周一");
        System.out.println(JsonUtil.toFullJson(nerBean));

        if (ListUtil.isNotEmpty(nerBean.getEntity())) {
            for (List<Object> list : nerBean.getEntity()) {
                String entity = (String) list.get(2);
                if (StringUtil.equals(entity, "time")) {
                    int start = (Integer) list.get(0);
                    int end = (Integer) list.get(1);
                    List<String> word = nerBean.getWord().subList(start, end);
                    String time = cn.wuxia.common.util.StringUtil.join(word, "");
                    System.out.println(time);

                    System.out.println(BosonUtil.time(time));
                }
            }
        }
    }

    @Test
    public void getctx() {
        try {
            Method method = ReflectionUtil.getAccessibleMethod(TestStaticClass.class, "efg", String.class);
            System.out.println("调用方法{}" + method.getName());
            System.out.println(method.invoke(null, "1234"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args){
        String result = "";

        String str = "测试文字<img src=\"/upload/2016-05/2016052314264421.jpg\" alt=\"\" title=\"\" />测试文字" +
                "<img title=\"\" src=\"/upload/2016-05/2016052314262994.png\" alt=\"\" />测试文字" +
                "<img src='/upload/2016-05/2016052314262994.jpg' alt=\"\" title=\"\" />测试文字";
        //在内容中匹配与正则表达式匹配的字符
        Pattern p =  Pattern.compile("(i?)<img.*? src=?(.*?\\.(jpg|gif|bmp|bnp|png)).*? />");
        Matcher m = p.matcher(str);

        while (m.find()) {
            String k = m.group();
            //循环匹配到的字符
            System.out.println(k);
            int srcIndex = k.indexOf("src=");
            String strSrc = cn.wuxia.common.util.StringUtil.substring(k, srcIndex+4);
            System.out.println(strSrc);

        }


        List<String> abc = Lists.newArrayList("abc", "efg", "xyz");
        for(String k : abc){
            if(k.equals("efg")){
                abc.remove(k);
            }
        }
        System.out.println(abc);
    }
}
