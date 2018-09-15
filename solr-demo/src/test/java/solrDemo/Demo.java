package solrDemo;

public class Demo {

    public class Inner{

    }

    public static class Innerr{

    }

    public void test(){
        final String str="kk";
        class Inner2{
            public void print(){
                System.out.println(str);
            }
        }
    }

}

class Test{
    public static void main(String[] args) {
        Demo d=new Demo();
        Demo.Inner inner=d.new Inner();

        Demo.Innerr innerr=new Demo.Innerr();
        float a=20.0f;
        int b=20;
        System.out.println(1==1l);
    }
}
