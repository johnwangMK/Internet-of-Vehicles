package cn.guet.haojiayou.db;

import java.util.ArrayList;
import cn.guet.haojiayou.bean.CarInfo;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 汽车信息数据库操作层
 * 
 * @author Zouwei
 * 
 */
public class CarInfoDao {

	private MyDBHelper dbHelper;

	public CarInfoDao(Context context) {
		dbHelper = new MyDBHelper(context);
	}

	/**
	 * 添加汽车信息列表
	 * 
	 * @param user
	 */
	public void insertInfo(ArrayList<CarInfo> list) {

		if (null != list && !list.isEmpty()) {

			SQLiteDatabase db = dbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			for (CarInfo carInfo : list) {

				values.put(DBInfo.CarInfoTable.USER_NO, carInfo.userNo);
				values.put(DBInfo.CarInfoTable.BRAND, carInfo.brand);
				values.put(DBInfo.CarInfoTable.MODEL, carInfo.model);
				values.put(DBInfo.CarInfoTable.LOGO, carInfo.logo);
				values.put(DBInfo.CarInfoTable.PLATE_NO, carInfo.plateNo);
				values.put(DBInfo.CarInfoTable.VEHICLE_ID_NO,
						carInfo.vehicleIDNo);
				values.put(DBInfo.CarInfoTable.ENGINE_NO, carInfo.engineNo);
				values.put(DBInfo.CarInfoTable.CAR_DOOR, carInfo.carDoor);
				values.put(DBInfo.CarInfoTable.CAR_SEAT, carInfo.carSeat);
				values.put(DBInfo.CarInfoTable.CAR_COLOR, carInfo.carColor);
				values.put(DBInfo.CarInfoTable.MILEAGE, carInfo.mileage);
				values.put(DBInfo.CarInfoTable.OILCONSUMPTION,
						carInfo.oilConsumption);
				values.put(DBInfo.CarInfoTable.ENGINE_PERFORMANCE,
						carInfo.enginePerformance);
				values.put(DBInfo.CarInfoTable.VARIATORPERFPRMANCE,
						carInfo.variatorPerformance);
				values.put(DBInfo.CarInfoTable.CAR_LAMP, carInfo.carLamp);

				db.insert(DBInfo.CAR_INFO_TABLE, null, values);

			}
			db.close();
		}
	}

	/**
	 * 根据用户名查询其汽车信息
	 * 
	 * @return
	 */
	public ArrayList<CarInfo> findCarsByUno(String userno) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		ArrayList<CarInfo> list = new ArrayList<CarInfo>();
		CarInfo car = null;
		Cursor cursor = db.query(DBInfo.CAR_INFO_TABLE, null,
				DBInfo.CarInfoTable.USER_NO + " =? ", new String[] { userno },
				null, null, null);

		while (cursor.moveToNext()) {

			String UserNo = cursor.getString(cursor
					.getColumnIndex(DBInfo.CarInfoTable.USER_NO));
			String Brand = cursor.getString(cursor
					.getColumnIndex(DBInfo.CarInfoTable.BRAND));
			String Model = cursor.getString(cursor
					.getColumnIndex(DBInfo.CarInfoTable.MODEL));
			String Logo = cursor.getString(cursor
					.getColumnIndex(DBInfo.CarInfoTable.LOGO));
			String PlateNo = cursor.getString(cursor
					.getColumnIndex(DBInfo.CarInfoTable.PLATE_NO));
			String VehicleIDNo = cursor.getString(cursor
					.getColumnIndex(DBInfo.CarInfoTable.VEHICLE_ID_NO));
			String EngineNo = cursor.getString(cursor
					.getColumnIndex(DBInfo.CarInfoTable.ENGINE_NO));
			String CarDoor = cursor.getString(cursor
					.getColumnIndex(DBInfo.CarInfoTable.CAR_COLOR));
			String CarColor = cursor.getString(cursor
					.getColumnIndex(DBInfo.CarInfoTable.CAR_SEAT));
			String CarSeat = cursor.getString(cursor
					.getColumnIndex(DBInfo.CarInfoTable.CAR_COLOR));
			String Mileage = cursor.getString(cursor
					.getColumnIndex(DBInfo.CarInfoTable.MILEAGE));
			String OilConsumption = cursor.getString(cursor
					.getColumnIndex(DBInfo.CarInfoTable.OILCONSUMPTION));
			String EnginePerformance = cursor.getString(cursor
					.getColumnIndex(DBInfo.CarInfoTable.ENGINE_PERFORMANCE));
			String VariatorPerformance = cursor.getString(cursor
					.getColumnIndex(DBInfo.CarInfoTable.VARIATORPERFPRMANCE));
			String CarLamp = cursor.getString(cursor
					.getColumnIndex(DBInfo.CarInfoTable.CAR_LAMP));

			car = new CarInfo(UserNo, Brand, Logo, Model, PlateNo, VehicleIDNo,
					EngineNo, CarDoor, CarSeat, CarColor, Mileage,
					OilConsumption, EnginePerformance, VariatorPerformance,
					CarLamp);

			list.add(car);
		}

		cursor.close();
		db.close();

		return list;
	}

	/**
	 * 查询所有的汽车信息
	 * 
	 * @return
	 */
	public ArrayList<CarInfo> findAllCars() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		ArrayList<CarInfo> list = new ArrayList<CarInfo>();
		CarInfo car = null;
		Cursor cursor = db.query(DBInfo.CAR_INFO_TABLE, null, null, null, null,
				null, null);

		while (cursor.moveToNext()) {

			String UserNo = cursor.getString(cursor
					.getColumnIndex(DBInfo.CarInfoTable.USER_NO));
			String Brand = cursor.getString(cursor
					.getColumnIndex(DBInfo.CarInfoTable.BRAND));
			String Model = cursor.getString(cursor
					.getColumnIndex(DBInfo.CarInfoTable.MODEL));
			String Logo = cursor.getString(cursor
					.getColumnIndex(DBInfo.CarInfoTable.LOGO));
			String PlateNo = cursor.getString(cursor
					.getColumnIndex(DBInfo.CarInfoTable.PLATE_NO));
			String VehicleIDNo = cursor.getString(cursor
					.getColumnIndex(DBInfo.CarInfoTable.VEHICLE_ID_NO));
			String EngineNo = cursor.getString(cursor
					.getColumnIndex(DBInfo.CarInfoTable.ENGINE_NO));
			String CarDoor = cursor.getString(cursor
					.getColumnIndex(DBInfo.CarInfoTable.CAR_COLOR));
			String CarColor = cursor.getString(cursor
					.getColumnIndex(DBInfo.CarInfoTable.CAR_SEAT));
			String CarSeat = cursor.getString(cursor
					.getColumnIndex(DBInfo.CarInfoTable.CAR_COLOR));
			String Mileage = cursor.getString(cursor
					.getColumnIndex(DBInfo.CarInfoTable.MILEAGE));
			String OilConsumption = cursor.getString(cursor
					.getColumnIndex(DBInfo.CarInfoTable.OILCONSUMPTION));
			String EnginePerformance = cursor.getString(cursor
					.getColumnIndex(DBInfo.CarInfoTable.ENGINE_PERFORMANCE));
			String VariatorPerformance = cursor.getString(cursor
					.getColumnIndex(DBInfo.CarInfoTable.VARIATORPERFPRMANCE));
			String CarLamp = cursor.getString(cursor
					.getColumnIndex(DBInfo.CarInfoTable.CAR_LAMP));

			car = new CarInfo(UserNo, Brand, Logo, Model, PlateNo, VehicleIDNo,
					EngineNo, CarDoor, CarSeat, CarColor, Mileage,
					OilConsumption, EnginePerformance, VariatorPerformance,
					CarLamp);

			list.add(car);
		}

		System.out.println("本地数据库查找所有" + list);
		cursor.close();
		db.close();

		return list;
	}

	/**
	 * 更新用户汽车信息
	 * 
	 * @param user
	 */
	public void updateCarByUno(ArrayList<CarInfo> list, String userno) {
		if (null != list && !list.isEmpty()) {

			SQLiteDatabase db = dbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			for (CarInfo carInfo : list) {

				values.put(DBInfo.CarInfoTable.USER_NO, carInfo.userNo);
				values.put(DBInfo.CarInfoTable.BRAND, carInfo.brand);
				values.put(DBInfo.CarInfoTable.MODEL, carInfo.model);
				values.put(DBInfo.CarInfoTable.LOGO, carInfo.logo);
				values.put(DBInfo.CarInfoTable.PLATE_NO, carInfo.plateNo);
				values.put(DBInfo.CarInfoTable.VEHICLE_ID_NO,
						carInfo.vehicleIDNo);
				values.put(DBInfo.CarInfoTable.ENGINE_NO, carInfo.engineNo);
				values.put(DBInfo.CarInfoTable.CAR_DOOR, carInfo.carDoor);
				values.put(DBInfo.CarInfoTable.CAR_SEAT, carInfo.carSeat);
				values.put(DBInfo.CarInfoTable.CAR_COLOR, carInfo.carColor);
				values.put(DBInfo.CarInfoTable.MILEAGE, carInfo.mileage);
				values.put(DBInfo.CarInfoTable.OILCONSUMPTION,
						carInfo.oilConsumption);
				values.put(DBInfo.CarInfoTable.ENGINE_PERFORMANCE,
						carInfo.enginePerformance);
				values.put(DBInfo.CarInfoTable.VARIATORPERFPRMANCE,
						carInfo.variatorPerformance);
				values.put(DBInfo.CarInfoTable.CAR_LAMP, carInfo.carLamp);

				db.update(DBInfo.CAR_INFO_TABLE, values,
						DBInfo.CarInfoTable.USER_NO + "=?",
						new String[] { userno });
				db.close();
			}
		}
	}

	/**
	 * 删除所有汽车信息
	 */
	public void delelteAll() {

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.delete(DBInfo.CAR_INFO_TABLE, null, null);
	}

	/**
	 * 根据车牌删除某辆汽车
	 */
	public void delelteCarByPlateNo(String PlateNo) {

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.delete(DBInfo.CAR_INFO_TABLE, DBInfo.CarInfoTable.PLATE_NO + "=?",
				new String[] { PlateNo });

	}
}
