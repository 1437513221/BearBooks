package com.xiong.bearbooks;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.xiong.bearbooks.db.Category;
import com.xiong.bearbooks.db.Journal;
import com.xiong.bearbooks.util.DatabaseTools;

import org.litepal.crud.DataSupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PieChart_fragment extends Fragment {
    List<String> categoryNameList=new ArrayList<>();//种类名list
    private List<Float> categorySumOfSumList=new ArrayList<>(); //单个种类金额和所有支出的百分比list
    private String leftTime;
    private String rightTime;
    private String beginTime;
    private String endTime;
    private long beginTimeInMillis;
    private long endTimeInMillis;
    private int categoryNameListSize;
    private int spendingSum; //总支出的总金额
    private int sum;
    private PieChart pieChart;
    private int bookId;
    private int categoryId;
    private TextView descriptionOfPieChart_text;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.piechart_fragment,container,false);
        pieChart=view.findViewById(R.id.pieChart);
        descriptionOfPieChart_text=view.findViewById(R.id.descriptionOfPieChart_text);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(new Date());
        StatementActivity statementActivity= (StatementActivity) getActivity();
        bookId=statementActivity.getBookId();
        descriptionOfPieChart_text.setText("默认为"+(calendar.get(Calendar.MONTH)+1)+"月的支出占比，其他情况可调整时间段");
        getDateOfPieChart();
        drawPieChart();
    }

    private void getDateOfPieChart(){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar calendar1=Calendar.getInstance();
        Calendar calendar2=Calendar.getInstance();
        Calendar calendar3=Calendar.getInstance();
        Calendar calendar4=Calendar.getInstance();
        StatementActivity statementActivity= (StatementActivity) getActivity();
        if (statementActivity.getStartTimeOfPieChart_text().getText().toString().equals("")||statementActivity.getEndTimeOfPieChart_text().getText().toString().equals("")){
            calendar1.setTime(new Date());
            beginTime=calendar1.get(Calendar.YEAR)+"-"+(calendar1.get(Calendar.MONTH)+1)+"-"+"1"+" "+"00"+":"+"00";
            if (calendar1.get(Calendar.MONTH)==11){
                endTime=(calendar1.get(Calendar.YEAR)+1)+"-"+"1"+"-"+"1"+" "+"00"+":"+"00";
            }else {endTime=calendar1.get(Calendar.YEAR)+"-"+(calendar1.get(Calendar.MONTH)+2)+"-"+"1"+" "+"00"+":"+"00";}
            try {
                calendar2.setTime(simpleDateFormat.parse(beginTime));
                calendar3.setTime(simpleDateFormat.parse(endTime));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            beginTimeInMillis=calendar2.getTimeInMillis();
            endTimeInMillis=calendar3.getTimeInMillis();
        }else {
            leftTime=statementActivity.getStartTimeOfPieChart_text().getText().toString()+" "+"00"+":"+"00";
            rightTime=statementActivity.getEndTimeOfPieChart_text().getText().toString()+" "+"00"+":"+"00";
            try {
                calendar1.setTime(simpleDateFormat.parse(leftTime));
                calendar2.setTime(simpleDateFormat.parse(rightTime));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long timeInMillis1=calendar1.getTimeInMillis();
            long timeInMillis2=calendar2.getTimeInMillis();
            if (timeInMillis1>=timeInMillis2){
                beginTime=statementActivity.getEndTimeOfPieChart_text().getText().toString()+" "+"00"+":"+"00";
                endTime=statementActivity.getStartTimeOfPieChart_text().getText().toString()+" "+"59"+":"+"59";
                try {
                    calendar3.setTime(simpleDateFormat.parse(beginTime));
                    calendar4.setTime(simpleDateFormat.parse(endTime));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                beginTimeInMillis=calendar3.getTimeInMillis();
                endTimeInMillis=calendar4.getTimeInMillis();
            }else {
                beginTime=statementActivity.getStartTimeOfPieChart_text().getText().toString()+" "+"00"+":"+"00";
                endTime=statementActivity.getEndTimeOfPieChart_text().getText().toString()+" "+"59"+":"+"59";
                try {
                    calendar3.setTime(simpleDateFormat.parse(beginTime));
                    calendar4.setTime(simpleDateFormat.parse(endTime));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                beginTimeInMillis=calendar3.getTimeInMillis();
                endTimeInMillis=calendar4.getTimeInMillis();
            }
        }
        Log.d("beginTime", beginTime);
        Log.d("endTime", endTime);
        Log.d("beginTimeInMillis", String.valueOf(beginTimeInMillis));
        Log.d("endTimeInMillis", String.valueOf(endTimeInMillis));
        getCategoryList(categoryNameList);
        categoryNameListSize=categoryNameList.size();
        spendingSum= DataSupport.where("bookId=? and type=? and date>? and date<?",String.valueOf(bookId),"1",String.valueOf(beginTimeInMillis),String.valueOf(endTimeInMillis)).sum(Journal.class,"amount",Integer.class);
        Log.d("spendingSum", String.valueOf(spendingSum));
        for (String s:categoryNameList){
            categoryId= DatabaseTools.findCategoryIdByName(s);
            sum=DataSupport.where("bookId=? and categoryId=? and type=? and date>? and date<?",String.valueOf(bookId),String.valueOf(categoryId),"1",String.valueOf(beginTimeInMillis),String.valueOf(endTimeInMillis)).sum(Journal.class,"amount",Integer.class);
            categorySumOfSumList.add(getFloat(sum,spendingSum));
        }
    }
    private void drawPieChart() {
        pieChart.setUsePercentValues(true); //设置为显示百分比
        pieChart.setDescription("支出分配比例图");//设置描述
        pieChart.setDescriptionTextSize(12f);
        // pieChart1.setExtraOffsets(5, 5, 5, 5);//设置饼状图距离上下左右的偏移量
        pieChart.setDrawCenterText(true); //设置可以绘制中间的文字
        pieChart.setCenterTextColor(Color.BLACK); //中间的文本颜色
        pieChart.setCenterTextSize(16);  //设置中间文本文字的大小
        pieChart.setDrawHoleEnabled(true); //绘制中间的圆形
        pieChart.setHoleColor(Color.WHITE);//饼状图中间的圆的绘制颜色
        pieChart.setHoleRadius(60f);//饼状图中间的圆的半径大小
        pieChart.setTransparentCircleColor(Color.WHITE);//设置圆环的颜色
        pieChart.setTransparentCircleAlpha(100);//设置圆环的透明度[0,255]
        pieChart.setTransparentCircleRadius(60f);//设置圆环的半径值
        pieChart.setRotationEnabled(true);//设置饼状图是否可以旋转(默认为true)
        pieChart.setRotationAngle(10);//设置饼状图旋转的角度

        Legend l = pieChart.getLegend(); //设置比例图
        l.setMaxSizePercent(100);
        l.setTextSize(12);
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);//设置每个tab的显示位置（这个位置是指下图右边小方框部分的位置 ）
        l.setXEntrySpace(10f);
        l.setYEntrySpace(10f);//设置tab之间Y轴方向上的空白间距值
        l.setYOffset(0f);

        //饼状图上字体的设置
        pieChart.setDrawEntryLabels(false);//设置是否绘制Label
        pieChart.setEntryLabelTextSize(23f);//设置绘制Label的字体大小


        //设置数据百分比和描述
        ArrayList<PieEntry> pieEntries = new ArrayList<PieEntry>();
        for (int i=0;i<categoryNameListSize;i++){
            pieEntries.add(new PieEntry(categorySumOfSumList.get(i),categoryNameList.get(i)));
        }

        String centerText = "支出占比";
        pieChart.setCenterText(centerText);//设置圆环中间的文字
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        ArrayList<Integer> colors = new ArrayList<>();

        colors.add(Color.rgb(0, 255, 0));
        colors.add(Color.rgb(255, 255, 0));
        colors.add(Color.rgb(255, 0, 0));
        colors.add(Color.rgb(255, 0, 255));
        colors.add(Color.rgb(244, 164, 96));
        colors.add(Color.rgb(30, 144, 255));
        colors.add(Color.rgb(151, 255, 255));
        colors.add(Color.rgb(124, 205, 124));
        colors.add(Color.rgb(255, 106, 106));
        pieDataSet.setColors(colors);

        pieDataSet.setSliceSpace(0f);//设置选中的Tab离两边的距离
        pieDataSet.setSelectionShift(5f);//设置选中的tab的多出来的
        PieData pieData = new PieData();
        pieData.setDataSet(pieDataSet);

        //各个饼状图所占比例数字的设置
        pieData.setValueFormatter(new PercentFormatter());//设置%
        pieData.setValueTextSize(18f);
        pieData.setValueTextColor(Color.BLUE);

        pieChart.setData(pieData);
        pieChart.highlightValues(null);
        pieChart.invalidate();
    }
    public static float getFloat(int a,int b) {
        if (b==0){
            b=1;
        }
        return (float) (Math.round(a*10000/b)/100.0);
    }

    private void getCategoryList(List<String> list){
        List<Category> categoryList= DataSupport.findAll(Category.class);
        for (Category category:categoryList){
            list.add(category.getName());
        }
    }

}
