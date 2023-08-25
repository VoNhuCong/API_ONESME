package com.vmt.model;

import org.json.JSONArray;
import org.json.JSONObject;

public class ChildChartData {
    public JSONObject data_parent;

    public ChildChartData(String rawData) {
        JSONObject originData = new JSONObject(rawData);

        JSONObject title = new JSONObject();
        title.put("show", true);
        title.put("text", makeTitleText(originData.getString("status_display"), originData.getString("host")));

        JSONObject legend = new JSONObject();
        legend.put("type", "scroll");
        legend.put("orient", "vertical");
        legend.put("right", 10);
        legend.put("top", "middle");
        legend.put("bottom", 20);

        JSONObject tooltip = new JSONObject();
        tooltip.put("trigger", "item");

        // make series
        JSONArray series = new JSONArray();
        JSONObject series_1 = new JSONObject();
        series_1.put("type", "pie");
        int[] radius_arr = { 50, 150 };
        series_1.put("radius", radius_arr);
        String[] center_arr = { "50%", "50%" };
        series_1.put("center", center_arr);
        JSONObject itemStyle = new JSONObject();
        itemStyle.put("borderRadius", 10);
        itemStyle.put("borderColor", "#fff");
        itemStyle.put("borderWidth", 2);
        series_1.put("itemStyle", itemStyle);
        series_1.put("data", makeDataCells(originData));
        series.put(series_1);
        // end series

        data_parent = new JSONObject();
        JSONObject option_chart = new JSONObject();
        option_chart.put("title", title);
        option_chart.put("legend", legend);
        option_chart.put("tooltip", tooltip);
        option_chart.put("series", series);

        JSONObject subData = new JSONObject();
        subData.put("option_chart", option_chart);
        subData.put("host", originData.getString("host"));

        JSONArray data = new JSONArray();
        data.put(subData);

        data_parent.put("data", data);
    }

    public String makeTitleText(String status_display, String host) {
        return (status_display == "Blacklist") ? host + " không phù hợp với trẻ em"
                : host + " có thể phù hợp với trẻ em";
    }

    public JSONArray makeDataCells(JSONObject data) {
        JSONArray jsonArr = new JSONArray();
        if (data.getString("alcohol_cigarette") != "0") {
            jsonArr.put(makeDataCell(data.getString("alcohol_cigarette"), "alcohol_cigarette"));
        }
        if (data.getString("drug_heroin") != "0") {
            jsonArr.put(makeDataCell(data.getString("drug_heroin"), "drug_heroin"));
        }
        if (data.getString("gory_horror") != "0") {
            jsonArr.put(makeDataCell(data.getString("gory_horror"), "gory_horror"));
        }
        if (data.getString("neutral") != "0") {
            jsonArr.put(makeDataCell(data.getString("neutral"), "neutral"));
        }
        if (data.getString("porn") != "0") {
            jsonArr.put(makeDataCell(data.getString("porn"), "porn"));
        }
        if (data.getString("sexy") != "0") {
            jsonArr.put(makeDataCell(data.getString("sexy"), "sexy"));
        }
        if (data.getString("weapon") != "0") {
            jsonArr.put(makeDataCell(data.getString("weapon"), "weapon"));
        }
        return jsonArr;
    }

    public JSONObject makeDataCell(String value, String name) {
        JSONObject data = new JSONObject();
        JSONObject color = new JSONObject();
        String namestr = "";
        String colorStr = "";
        switch (name) {
            case "neutral":
                namestr = "Trung lập";
                colorStr = "#6BBCD1";
                break;
            case "porn":
                namestr = "Khiêu dâm";
                colorStr = "#E23201";
                break;
            case "weapon":
                namestr = "Vũ khí";
                colorStr = "#B44747";
                break;
            case "drug_heroin":
                namestr = "Ma túy, heroin";
                colorStr = "#690707";
                break;
            case "alcohol_cigarette":
                namestr = "Rượu, thuốc lá";
                colorStr = "#5470C6";
                break;
            case "sexy":
                namestr = "Gợi cảm";
                colorStr = "#002947";
                break;
            case "gory_horror":
                namestr = "Kinh dị, đẫm máu";
                colorStr = "#FD9415";
                break;
            default:
                break;
        }
        data.put("name", namestr);
        data.put("value", value);
        color.put("color", colorStr);
        data.put("itemStyle", color);
        return data;
    }

    public JSONObject getData_parent() {
        return data_parent;
    }

}
