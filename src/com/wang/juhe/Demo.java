package com.wang.juhe;

public class Demo {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
			StationInfo info = Juhe.getRequest(110.344332,25.289676);//110.344187,25.2894
			Data[] data = null;
			data = info.getResult().getData();
			for(int i=0;i<data.length;i++){
				Object obj =data[i].getGastprice();
				System.out.println(data[i].getGastprice().getP97());
				
			}
			//System.out.print(info);
	}

}
