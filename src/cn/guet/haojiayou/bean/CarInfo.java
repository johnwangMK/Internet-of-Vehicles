package cn.guet.haojiayou.bean;

import java.io.Serializable;

public class CarInfo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** 用户注册手机号 */
	public String userNo;
	/** 汽车品牌 */
    public String brand;
    /** 汽车标志 */
    public String logo;
    /** 汽车型号 */
    public String model;
    /** 车牌号码*/
    public String plateNo;
    /** 车架号*/
    public String vehicleIDNo;
    /** 发动机号（8位） */
    public String engineNo;    
	/** 车门数 */
    public String carDoor;
    /** 座位数 */
    public String carSeat;
    /** 里程数 */
    public String carColor;
    /** 车身颜色 */
    public String mileage;
    /** 剩余汽油量（百分比） */
    public String oilConsumption;
    /** 汽油总量 */
    public String oilTotal;
    /** 发动机性能（好、异常） */
    public String enginePerformance;
    /** 变速器性能（好、异常） */
    public String variatorPerformance;
    /** 车灯（好、坏）*/
    public String carLamp;    
	
    public CarInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CarInfo(String userNo, String brand, String logo, String model,
			String plateNo, String vehicleIDNo, String engineNo,
			String carDoor, String carSeat, String carColor, String mileage,
			String oilConsumption, String oilTotal, String enginePerformance,
			String variatorPerformance, String carLamp) {
		super();
		this.userNo = userNo;
		this.brand = brand;
		this.logo = logo;
		this.model = model;
		this.plateNo = plateNo;
		this.vehicleIDNo = vehicleIDNo;
		this.engineNo = engineNo;
		this.carDoor = carDoor;
		this.carSeat = carSeat;
		this.carColor = carColor;
		this.mileage = mileage;
		this.oilConsumption = oilConsumption;
		this.oilTotal = oilTotal;
		this.enginePerformance = enginePerformance;
		this.variatorPerformance = variatorPerformance;
		this.carLamp = carLamp;
	}
	
	

	public CarInfo(String brand, String logo, String model, String plateNo,
			String vehicleIDNo, String engineNo, String carDoor,
			String carSeat, String carColor, String mileage,
			String oilConsumption, String oilTotal, String enginePerformance,
			String variatorPerformance, String carLamp) {
		super();
		this.brand = brand;
		this.logo = logo;
		this.model = model;
		this.plateNo = plateNo;
		this.vehicleIDNo = vehicleIDNo;
		this.engineNo = engineNo;
		this.carDoor = carDoor;
		this.carSeat = carSeat;
		this.carColor = carColor;
		this.mileage = mileage;
		this.oilConsumption = oilConsumption;
		this.oilTotal = oilTotal;
		this.enginePerformance = enginePerformance;
		this.variatorPerformance = variatorPerformance;
		this.carLamp = carLamp;
	}

	@Override
	public String toString() {
		return "CarInfo [userNo=" + userNo + ", brand=" + brand + ", logo="
				+ logo + ", model=" + model + ", plateNo=" + plateNo
				+ ", vehicleIDNo=" + vehicleIDNo + ", engineNo=" + engineNo
				+ ", carDoor=" + carDoor + ", carSeat=" + carSeat
				+ ", carColor=" + carColor + ", mileage=" + mileage
				+ ", oilConsumption=" + oilConsumption + ", oilTotal="
				+ oilTotal + ", enginePerformance=" + enginePerformance
				+ ", variatorPerformance=" + variatorPerformance + ", carLamp="
				+ carLamp + "]";
	}

	public String getUserNo() {
		return userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getPlateNo() {
		return plateNo;
	}

	public void setPlateNo(String plateNo) {
		this.plateNo = plateNo;
	}

	public String getVehicleIDNo() {
		return vehicleIDNo;
	}

	public void setVehicleIDNo(String vehicleIDNo) {
		this.vehicleIDNo = vehicleIDNo;
	}

	public String getEngineNo() {
		return engineNo;
	}

	public void setEngineNo(String engineNo) {
		this.engineNo = engineNo;
	}

	public String getCarDoor() {
		return carDoor;
	}

	public void setCarDoor(String carDoor) {
		this.carDoor = carDoor;
	}

	public String getCarSeat() {
		return carSeat;
	}

	public void setCarSeat(String carSeat) {
		this.carSeat = carSeat;
	}

	public String getCarColor() {
		return carColor;
	}

	public void setCarColor(String carColor) {
		this.carColor = carColor;
	}

	public String getMileage() {
		return mileage;
	}

	public void setMileage(String mileage) {
		this.mileage = mileage;
	}

	public String getOilConsumption() {
		return oilConsumption;
	}

	public void setOilConsumption(String oilConsumption) {
		this.oilConsumption = oilConsumption;
	}

	public String getOilTotal() {
		return oilTotal;
	}

	public void setOilTotal(String oilTotal) {
		this.oilTotal = oilTotal;
	}

	public String getEnginePerformance() {
		return enginePerformance;
	}

	public void setEnginePerformance(String enginePerformance) {
		this.enginePerformance = enginePerformance;
	}

	public String getVariatorPerformance() {
		return variatorPerformance;
	}

	public void setVariatorPerformance(String variatorPerformance) {
		this.variatorPerformance = variatorPerformance;
	}

	public String getCarLamp() {
		return carLamp;
	}

	public void setCarLamp(String carLamp) {
		this.carLamp = carLamp;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	
}
