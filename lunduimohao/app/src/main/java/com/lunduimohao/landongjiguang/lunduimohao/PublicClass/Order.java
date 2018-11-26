package com.lunduimohao.landongjiguang.lunduimohao.PublicClass;

import org.json.JSONObject;

import java.io.Serializable;

public class Order implements Serializable {

	public String WheelId;

	public String TreadWear;

	public String WheelThick;

	public String RimWidth;
	public String RimThick;

	public String Time;


	public Order(String WheelId, String TreadWear, String WheelThick, String RimWidth, String RimThick, String Time) {
		this.WheelId = WheelId;
		this.TreadWear = TreadWear;
		this.WheelThick = WheelThick;

		this.RimWidth = RimWidth;
		this.RimThick = RimThick;

		this.Time = Time;
	}

	public Order(JSONObject obj) {
		this.WheelId = obj.optString("WheelId");
		this.TreadWear = obj.optString("TreadWear");
		this.WheelThick = obj.optString("WheelThick");
		this.RimWidth = obj.optString("RimWidth");
		this.RimThick = obj.optString("RimThick");
		this.Time = obj.optString("Time");
	}
}
