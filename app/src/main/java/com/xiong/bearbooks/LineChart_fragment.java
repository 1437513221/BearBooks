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

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.xiong.bearbooks.db.Journal;
import com.xiong.bearbooks.util.DatabaseTools;

import org.litepal.crud.DataSupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LineChart_fragment extends Fragment {
    private int maxOfMonth;
    private String beginDateString;
    private String endDateString;
    List<Integer> dailySpending=new ArrayList<>();
    private LineChart lineChart;
    private TextView descriptionOfLineChart_text;
    private int bookId;
    private String monthDate;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.linechart_fragment,container,false);
        lineChart=view.findViewById(R.id.lineChart);
        descriptionOfLineChart_text=view.findViewById(R.id.descriptionOfLineChart_text);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(new Date());
        descriptionOfLineChart_text.setText("默认为"+(calendar.get(Calendar.MONTH)+1)+"月的支出折线图，可调整时间段和支出种类");
        StatementActivity statementActivity= (StatementActivity) getActivity();
        bookId=statementActivity.getBookId();
        getDateOfLineChart();
        drawLineChar();

    }
    private void drawLineChar(){
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < maxOfMonth; i++)
            entries.add(new Entry(i,dailySpending.get(i)));

        LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset
        dataSet.setColor(Color.parseColor("#7d7d7d"));//线条颜色
        dataSet.setCircleColor(Color.parseColor("#7d7d7d"));//圆点颜色
        dataSet.setLineWidth(1f);//线条宽度

        //设置样式
        YAxis rightAxis = lineChart.getAxisRight();

        //设置图表右边的y轴禁用
        rightAxis.setEnabled(false);
        YAxis leftAxis = lineChart.getAxisLeft();
        //设置图表左边的y轴禁用
        leftAxis.setEnabled(true);
        //设置x轴
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setTextColor(Color.parseColor("#333333"));
        xAxis.setTextSize(11f);
        xAxis.setDrawAxisLine(true);//是否绘制轴线
        xAxis.setDrawGridLines(false);//设置x轴上每个点对应的线
        xAxis.setDrawLabels(true);//绘制标签  指x轴上的对应数值
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置x轴的显示位置
        xAxis.setGranularity(1f);//禁止放大后x轴标签重绘
        xAxis.setValueFormatter(new AxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.valueOf((int) value + 1).concat("日");
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });

        //chart设置数据
        LineData lineData = new LineData(dataSet);
        //是否绘制线条上的文字
        lineData.setDrawValues(true);
        StatementActivity statementActivity= (StatementActivity) getActivity();
        if (statementActivity.getCategoryOfLineChart_text().getText().toString().equals("")){
            String s="该月所有种类的支出折线图";
            lineChart.setDescription(s);
        }else {
            String s=statementActivity.getMonthOfLineChart_text().getText().toString()+"月"+statementActivity.getCategoryOfLineChart_text().getText().toString()+"的支出折线图";
            lineChart.setDescription(s);
        }

        lineChart.setScaleEnabled(false);
        lineChart.setData(lineData);
        lineChart.invalidate(); // refresh

    }
    private void getDateOfLineChart(){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        StatementActivity statementActivity= (StatementActivity) getActivity();
        Calendar calendar=Calendar.getInstance();
        if (statementActivity.getCategoryOfLineChart_text().getText().toString().equals("")){
            if (statementActivity.getYearOfLineChart_text().getText().toString().equals("")||statementActivity.getMonthOfLineChart_text().getText().toString().equals("")){
                calendar.setTime(new Date());
                maxOfMonth=calendar.getActualMaximum(Calendar.DATE);
                if (calendar.get(Calendar.MONTH)==11){
                    for (int i=0;i<maxOfMonth;i++){
                        beginDateString=calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.YEAR)+1)+"-"+(i+1)+" "+"00"+":"+"00";
                        if (i==maxOfMonth-1){
                            endDateString=(calendar.get(Calendar.YEAR)+1)+"-"+"1"+"-"+"1"+" "+"00"+":"+"00";
                        }else {
                            endDateString= calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+(i+2)+" "+"00"+":"+"00";

                        }
                        getDailySpending(null);}
                }else {
                    for (int i=0;i<maxOfMonth;i++){
                        beginDateString=calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+(i+1)+" "+"00"+":"+"00";
                        if (i==maxOfMonth-1){
                            endDateString=calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH)+2)+"-"+"1"+" "+"00"+":"+"00";
                        }else {
                            endDateString= calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+(i+2)+" "+"00"+":"+"00";
                        }
                        getDailySpending(null);}
                }
            }else {
                monthDate=statementActivity.getYearOfLineChart_text().getText().toString()+"-"+statementActivity.getMonthOfLineChart_text().getText().toString()+"-"+"1"+" "+"00"+":"+"00";
                try {
                    calendar.setTime(simpleDateFormat.parse(monthDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                maxOfMonth=calendar.getActualMaximum(Calendar.DATE);
                if (statementActivity.getMonthOfLineChart_text().getText().toString().equals("12")){
                    for (int i=0;i<maxOfMonth;i++){
                        beginDateString=statementActivity.getYearOfLineChart_text().getText().toString()+"-"+statementActivity.getMonthOfLineChart_text().getText().toString()+"-"+(i+1)+" "+"00"+":"+"00";
                        if (i==maxOfMonth-1){
                            int year= Integer.parseInt(statementActivity.getYearOfLineChart_text().getText().toString());
                            endDateString=(year+1)+"-"+"1"+"-"+"1"+" "+"00"+":"+"00";
                        }else {
                            endDateString= statementActivity.getYearOfLineChart_text().getText().toString()+"-"+statementActivity.getMonthOfLineChart_text().getText().toString()+"-"+(i+2)+" "+"00"+":"+"00";
                        }
                        getDailySpending(null);}
                }else {
                    for (int i=0;i<maxOfMonth;i++){
                        beginDateString=statementActivity.getYearOfLineChart_text().getText().toString()+"-"+statementActivity.getMonthOfLineChart_text().getText().toString()+"-"+(i+1)+" "+"00"+":"+"00";
                        if (i==maxOfMonth-1){
                            int month= Integer.parseInt(statementActivity.getMonthOfLineChart_text().getText().toString());
                            endDateString=statementActivity.getYearOfLineChart_text().getText().toString()+"-"+(month+1)+"-"+"1"+" "+"00"+":"+"00";
                        }else {
                            endDateString= statementActivity.getYearOfLineChart_text().getText().toString()+"-"+statementActivity.getMonthOfLineChart_text().getText().toString()+"-"+(i+2)+" "+"00"+":"+"00";
                        }
                        getDailySpending(null);}
                }
            }
        }else {
            if (statementActivity.getYearOfLineChart_text().getText().toString().equals("")||statementActivity.getMonthOfLineChart_text().getText().toString().equals("")){
                calendar.setTime(new Date());
                maxOfMonth=calendar.getActualMaximum(Calendar.DATE);
                if (calendar.get(Calendar.MONTH)==11){
                    for (int i=0;i<maxOfMonth;i++){
                        beginDateString=calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.YEAR)+1)+"-"+(i+1)+" "+"00"+":"+"00";
                        if (i==maxOfMonth-1){
                            endDateString=(calendar.get(Calendar.YEAR)+1)+"-"+"1"+"-"+"1"+" "+"00"+":"+"00";
                        }else {
                            endDateString= calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+(i+2)+" "+"00"+":"+"00";

                        }
                        getDailySpending(statementActivity.getCategoryOfLineChart_text().getText().toString());}
                }else {
                    for (int i=0;i<maxOfMonth;i++){
                        beginDateString=calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+(i+1)+" "+"00"+":"+"00";
                        if (i==maxOfMonth-1){
                            endDateString=calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH)+2)+"-"+"1"+" "+"00"+":"+"00";
                        }else {
                            endDateString= calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+(i+2)+" "+"00"+":"+"00";
                        }
                        getDailySpending(statementActivity.getCategoryOfLineChart_text().getText().toString());}
                }
            }else {
                monthDate=statementActivity.getYearOfLineChart_text().getText().toString()+"-"+statementActivity.getMonthOfLineChart_text().getText().toString()+"-"+"1"+" "+"00"+":"+"00";
                try {
                    calendar.setTime(simpleDateFormat.parse(monthDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                maxOfMonth=calendar.getActualMaximum(Calendar.DATE);
                if (statementActivity.getMonthOfLineChart_text().getText().toString().equals("12")){
                    for (int i=0;i<maxOfMonth;i++){
                        beginDateString=statementActivity.getYearOfLineChart_text().getText().toString()+"-"+statementActivity.getMonthOfLineChart_text().getText().toString()+"-"+(i+1)+" "+"00"+":"+"00";
                        if (i==maxOfMonth-1){
                            int year= Integer.parseInt(statementActivity.getYearOfLineChart_text().getText().toString());
                            endDateString=(year+1)+"-"+"1"+"-"+"1"+" "+"00"+":"+"00";
                        }else {
                            endDateString= statementActivity.getYearOfLineChart_text().getText().toString()+"-"+statementActivity.getMonthOfLineChart_text().getText().toString()+"-"+(i+2)+" "+"00"+":"+"00";
                        }
                        getDailySpending(statementActivity.getCategoryOfLineChart_text().getText().toString());}
                }else {
                    for (int i=0;i<maxOfMonth;i++){
                        beginDateString=statementActivity.getYearOfLineChart_text().getText().toString()+"-"+statementActivity.getMonthOfLineChart_text().getText().toString()+"-"+(i+1)+" "+"00"+":"+"00";
                        if (i==maxOfMonth-1){
                            int month= Integer.parseInt(statementActivity.getMonthOfLineChart_text().getText().toString());
                            endDateString=statementActivity.getYearOfLineChart_text().getText().toString()+"-"+(month+1)+"-"+"1"+" "+"00"+":"+"00";
                        }else {
                            endDateString= statementActivity.getYearOfLineChart_text().getText().toString()+"-"+statementActivity.getMonthOfLineChart_text().getText().toString()+"-"+(i+2)+" "+"00"+":"+"00";
                        }
                        getDailySpending(statementActivity.getCategoryOfLineChart_text().getText().toString());}
                }
            }
        }

    }

    private void getDailySpending(String categoryName){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (categoryName==null){
            try {
                Calendar calendar1=Calendar.getInstance();
                calendar1.setTime(simpleDateFormat.parse(beginDateString));
                long timeInMillis1=calendar1.getTimeInMillis();
                Calendar calendar2=Calendar.getInstance();
                calendar2.setTime(simpleDateFormat.parse(endDateString));
                long timeInMillis2=calendar2.getTimeInMillis();
                int dailyAmount= DataSupport.where(" bookId=? and type=? and date>? and date<?", String.valueOf(bookId), "1",String.valueOf(timeInMillis1),String.valueOf(timeInMillis2)).sum(Journal.class,"amount",Integer.class);
                dailySpending.add(dailyAmount);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else {
            int categoryId= DatabaseTools.findCategoryIdByName(categoryName);
            try {
                Calendar calendar1=Calendar.getInstance();
                calendar1.setTime(simpleDateFormat.parse(beginDateString));
                long timeInMillis1=calendar1.getTimeInMillis();
                Calendar calendar2=Calendar.getInstance();
                calendar2.setTime(simpleDateFormat.parse(endDateString));
                long timeInMillis2=calendar2.getTimeInMillis();
                int dailyAmount= DataSupport.where(" bookId=? and categoryId=? and type=? and date>? and date<?", String.valueOf(bookId), String.valueOf(categoryId),"1",String.valueOf(timeInMillis1),String.valueOf(timeInMillis2)).sum(Journal.class,"amount",Integer.class);
                dailySpending.add(dailyAmount);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }
}
