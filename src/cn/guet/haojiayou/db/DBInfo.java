package cn.guet.haojiayou.db;

public class DBInfo {

		
		// 数据库名称
		public static  final String DB_NAME="haojiayou.db3"; 
		
		// 数据库版本
		public static final int  VERSION =1;
		// 数据库表
		public static String CAR_INFO_TABLE = "CarInfo";
		
		
		/**
		 * 
		 * 汽车信息表	
		 *
		 */
	public static class CarInfoTable{
		
		public static String CAR_INFO_CREATE ="CREATE TABLE IF NOT EXISTS  " 
		+ CAR_INFO_TABLE  + " (UserNo VARCHAR(255),Brand VARCHAR(255),Model VARCHAR(255)," +
				"Logo VARCHAR(255),PlateNo VARCHAR(255) PRIMARY KEY,VehicleIDNo VARCHAR(255),EngineNo VARCHAR(255),CarDoor VARCHAR(255)," +
				"CarColor VARCHAR(255),CarSeat VARCHAR(255),Mileage VARCHAR(255),OilConsumption VARCHAR(255),EnginePerformance VARCHAR(255)," +
				"VariatorPerformance VARCHAR(255),CarLamp VARCHAR(255))";
		
		public static String CAR_INFO_DROP ="DROP TABLE IF  EXISTS " + CAR_INFO_TABLE ; 
		
		//public static String ID ="_id";
		public static String USER_NO ="UserNo";
		public static String BRAND ="Brand";
		public static String MODEL ="Model";
		public static String LOGO ="Logo";
		public static String PLATE_NO ="PlateNo";
		public static String VEHICLE_ID_NO ="VehicleIDNo";
		public static String ENGINE_NO ="EngineNo";
		public static String CAR_DOOR ="CarDoor";
		public static String CAR_COLOR ="CarColor";
		public static String CAR_SEAT ="CarSeat";
		public static String MILEAGE ="Mileage";
		public static String OILCONSUMPTION ="OilConsumption";
		public static String ENGINE_PERFORMANCE ="EnginePerformance";
		public static String VARIATORPERFPRMANCE ="VariatorPerformance";
		public static String CAR_LAMP="CarLamp";
		
		}
	
}
