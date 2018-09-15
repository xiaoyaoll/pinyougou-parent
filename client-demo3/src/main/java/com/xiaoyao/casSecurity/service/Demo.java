package com.xiaoyao.casSecurity.service;

/**
 * 冒泡排序,两两比较，大的右边小的左边
 */
@SuppressWarnings("ALL")
public class Demo {

    public static void main(String[] args) {
        int[] arr = {7, 8, 9, 4, 5,456,15,45};
        sort(arr, 0, arr.length - 1);
        //quickSort(arr, 0, arr.length - 1);
        for (int i : arr) {
            System.out.println(i);
        }

    }

    /*
    快速排序
     */
    public static void sort(int[] arr, int low, int high) {

        //方法出口
        if (low > high) {
            return;
        }

        int i = low;
        int j = high;
        int key = arr[low];

        //一轮比较开始
        while (i < j) {

            while (i < j && arr[j]>key) {
                j--;
            }

            while (i < j && arr[i]<=key) {
                i++;
            }
            //交换位置
            if (i < j) {
                int p = arr[i];
                arr[i] = arr[j];
                arr[j] = p;
            }
        }

        //调整key的位置
        int p = arr[i];
        arr[i] = arr[low];
        arr[low] = p;

        //递归调用
        sort(arr, low, i - 1);
        sort(arr, i + 1, high);
    }

    private static void quickSort(int[] a, int low, int high) {
        //1,找到递归算法的出口
        if (low > high) {
            return;
        }
        //2, 存
        int i = low;
        int j = high;
        //3,key
        int key = a[low];
        //4，完成一趟排序
        while (i < j) {

            // 4.2 从左往右找到第一个大于key的数
            while (i < j && a[i] <= key) {
                i++;
            }

            //4.1 ，从右往左找到第一个小于key的数
            while (i < j && a[j] > key) {
                j--;
            }
            //4.3 交换
            if (i < j) {
                int p = a[i];
                a[i] = a[j];
                a[j] = p;
            }
        }
        // 4.4，调整key的位置
        int p = a[i];
        a[i] = a[low];
        a[low] = p;
        //5, 对key左边的数快排
        quickSort(a, low, i - 1);
        //6, 对key右边的数快排
        quickSort(a, i + 1, high);
    }

}
