package cn.guet.haojiayou.bean;

import java.io.Serializable;

public class FillOilOrder implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 订单号 */
	public String orderID;
	/** 订单时间 */
	public String orderTime;
	/** 姓名 */
	public String carUserName;
	/** 联系电话 */
	public String phoneNum;
	/** 油站地址 */
	public String gasStationAddress;
	/** 油站名字 */
	public String gasStationName;
	/** 油品牌名字*/
	public String oilBrandName;
	/** 加油类型 */
	public String oilType;
	/** 单价 */
	public String oilPrice;
	/** 到达时间 */
	public String arriveTime;
	/** 总金额 */
	public String totalMoney;
	/** 车牌*/
	public String plateNo;
	/** 订单状态*/
	public String orderStatus;
		
	public FillOilOrder() {
		super();
	}

	public FillOilOrder(String orderTime, String carUserName, String phoneNum,
			String gasStationAddress, String gasStationName,
			String oilBrandName, String oilType, String oilPrice,
			String arriveTime, String totalMoney, String plateNo,
			String orderStatus) {
		super();
		this.orderTime = orderTime;
		this.carUserName = carUserName;
		this.phoneNum = phoneNum;
		this.gasStationAddress = gasStationAddress;
		this.gasStationName = gasStationName;
		this.oilBrandName = oilBrandName;
		this.oilType = oilType;
		this.oilPrice = oilPrice;
		this.arriveTime = arriveTime;
		this.totalMoney = totalMoney;
		this.plateNo = plateNo;
		this.orderStatus = orderStatus;
	}

	public FillOilOrder(String orderID, String orderTime, String carUserName,
			String phoneNum, String gasStationAddress, String gasStationName,
			String oilBrandName, String oilType, String oilPrice,
			String arriveTime, String totalMoney, String plateNo,
			String orderStatus) {
		super();
		this.orderID = orderID;
		this.orderTime = orderTime;
		this.carUserName = carUserName;
		this.phoneNum = phoneNum;
		this.gasStationAddress = gasStationAddress;
		this.gasStationName = gasStationName;
		this.oilBrandName = oilBrandName;
		this.oilType = oilType;
		this.oilPrice = oilPrice;
		this.arriveTime = arriveTime;
		this.totalMoney = totalMoney;
		this.plateNo = plateNo;
		this.orderStatus = orderStatus;
	}

	@Override
	public String toString() {
		return "FillOilOrder [orderID=" + orderID + ", orderTime=" + orderTime
				+ ", carUserName=" + carUserName + ", phoneNum=" + phoneNum
				+ ", gasStationAddress=" + gasStationAddress
				+ ", gasStationName=" + gasStationName + ", oilBrandName="
				+ oilBrandName + ", oilType=" + oilType + ", oilPrice="
				+ oilPrice + ", arriveTime=" + arriveTime + ", totalMoney="
				+ totalMoney + ", plateNo=" + plateNo + ", orderStatus="
				+ orderStatus + "]";
	}
	
	

	
}
