
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import org.json.simple.parser.*;
import org.json.simple.*;
import javax.net.ssl.HttpsURLConnection;

import org.json.simple.parser.JSONParser;

public class Detail {

	public static void main(String[] args)throws SQLException {
		
		Scanner sc = new Scanner(System.in); 
		
		Connection connect = null;
		Statement st = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		String url = "jdbc:postgresql://localhost:5432/";
		String db = "postgres";
		String user = "postgres";
		String passwd = "1234";
		int n;
		
		try {
			Scanner scan = new Scanner(System.in);
			System.out.println("SQL Progroject Start");
			System.out.println("Connecting PostgreSQL database");
			connect = DriverManager.getConnection(url,user,passwd);
			
			System.out.println("KindRestuarant, SafeRestuarant, Location");
			st = connect.createStatement();
			String create = "create table KindRestuarant(kName char(20), Kind char(10),KindNum int, kAddr char(70));"+
					"create table Park(pName char(40), Free char(20), Longitude float, Latitude float, pAddr char(70));"+
					"create table Location( Longitude float,Latitude float, lAddr char(70));";
			st.executeUpdate(create);
			
			
			
			parks_insert(connect,st,rs);
			
			
			kindRestuarants_insert(connect,st,rs);
			System.out.println("insert end");
			
			
			
			while(true) {
				System.out.println("----------------------------------------------------------------------------------------");
				System.out.println("1: 현재 위치 입력 -> 주변에 가격이 싼 음식점들 출력, 주변의 공영주차장들 출력");
				System.out.println("2: 현재 위치&음식 카테고리 입력 -> 카테고리에 맞게 주변에 가격이 싼 음식점들 출력, 주변의 공영주차장들 출력");
				System.out.println("3: 음식점 이름으로 입력 -> 입력이름과 비슷한 이름을 가진 가격이 싼 음식점들 출력 -> 음식점 선택 -> 선택한 음식점 주변 공영주차장들 출력");
				System.out.println("4: 종료");
				System.out.print("입력:");
				int N = scan.nextInt();
				System.out.println("----------------------------------------------------------------------------------------");
				
				//현재 위치 입력 -> 주변에 가격이 싼 음식점들 출력, 주변의 공영주차장들 출력 
				if(N==1) {
					System.out.println("현재 위치를 입력해 주십시오:");
					System.out.print("x: ");
					float x = scan.nextFloat();
					System.out.print("y: ");
					float y = scan.nextFloat();
					
					System.out.println("<주변에 가격이 싼 음식점 목록>");
					String Query1 = "select kName, Kind, kAddr " +
			                "from kindRestuarant " +
			                "join Location on kAddr = lAddr " +
			                "where Longitude between " + (x - 0.015) + " and " + (x + 0.015) + " " +
			                "and Latitude between " + (y - 0.015) + " and " + (y + 0.015);
					pst = connect.prepareStatement(Query1);
					rs = pst.executeQuery();
					
					System.out.println(String.format("%-30s %-30s %-30s", "음식점 이름","음식 카테고리","주소"));
					while(rs.next()) {
						System.out.println(String.format("%-30s %-30s %-30s ",rs.getString(1),rs.getString(2),rs.getString(3)));
					}
					
					System.out.println("<주변 공영주차장 목록>");
					
					String Query2 = "select distinct pName, free, pAddr " +
			                "from park " +
			                "where Longitude between " + (x - 0.015) + " and " + (x + 0.015) + " " +
			                "and Latitude between " + (y - 0.015) + " and " + (y + 0.015);
					pst = connect.prepareStatement(Query2);
					rs = pst.executeQuery();
					
					System.out.println(String.format("%-30s %-30s %-30s", "주차장 이름","무료/유료","주소"));
					while(rs.next()) {
						System.out.println(String.format("%-30s %-30s %-30s ",rs.getString(1),rs.getString(2),rs.getString(3)));
					}
					
				}
				//현재 위치&음식 카테고리 입력 -> 카테고리에 맞게 주변에 가격이 싼 음식점들 출력, 주변의 공영주차장들 출력
				else if(N==2) {
					System.out.println("현재 위치를 입력해 주십시오:");
					System.out.print("x: ");
					float x = scan.nextFloat();
					System.out.print("y: ");
					float y = scan.nextFloat();
					System.out.print("음식 카테고리(한식/중식/경양식.일식):");
					String category = scan.next();
					int c_num = -1;

					if(category.equals("한식")) c_num=1;
					else if(category.equals("중식")) c_num=2;
					else if(category.equals("양식")||category.equals("경양식")||category.equals("경양식,양식")) c_num=3;
					else {
						System.out.println("에러: 존재하지 않은 음식 카테고리 입니다.");
						continue;
					}
					
					System.out.println("<주변에 가격이 싼 음식점 목록>");
					String Query1 = "select kName, Kind, kAddr " +
			                "from kindRestuarant " +
			                "join Location on kAddr = lAddr " +
			                "where Longitude between " + (x - 0.015) + " and " + (x + 0.015) + " " +
			                "and Latitude between " + (y - 0.015) + " and " + (y + 0.015)+" "+
			                "and KindNum ="+c_num;
					pst = connect.prepareStatement(Query1);
					rs = pst.executeQuery();
					
					System.out.println(String.format("%-30s %-30s %-30s", "음식점 이름","음식 카테고리","주소"));
					while(rs.next()) {
						System.out.println(String.format("%-30s %-30s %-30s ",rs.getString(1),rs.getString(2),rs.getString(3)));
					}
					
					System.out.println("<주변 공영주차장 목록>");
					
					String Query2 = "select distinct pName, free, pAddr " +
			                "from park " +
			                "where Longitude between " + (x - 0.015) + " and " + (x + 0.015) + " " +
			                "and Latitude between " + (y - 0.015) + " and " + (y + 0.015);
					pst = connect.prepareStatement(Query2);
					rs = pst.executeQuery();
					
					System.out.println(String.format("%-30s %-30s %-30s", "주차장 이름","무료/유료","주소"));
					while(rs.next()) {
						System.out.println(String.format("%-30s %-30s %-30s ",rs.getString(1),rs.getString(2),rs.getString(3)));
					}
					
				}
				//음식점 이름으로 입력 -> 입력이름과 비슷한 이름을 가진 가격이 싼 음식점들 출력 ->음식점 선택 -> 선택한 음식점 주변 공영주차장들 출력 
				else if(N==3) {
					System.out.print("음식점 검색:");
					String name = scan.next();
					System.out.println("<음식점 검색 결과 목록>");
					
					String Query1 = "select kName, Kind, kAddr " +
			                "from kindRestuarant " +
			                "join Location on kAddr = lAddr " +
			                "where kName like '%" + name + "%'";
			                
					pst = connect.prepareStatement(Query1);
					rs = pst.executeQuery();
					
					System.out.println(String.format("%-30s %-30s %-30s", "음식점 이름","음식 카테고리","주소"));
					while(rs.next()) {
						System.out.println(String.format("%-30s %-30s %-30s ",rs.getString(1),rs.getString(2),rs.getString(3)));
					}
					System.out.print("음식점 선택:");
					String sel_name = scan.next();
					
					System.out.println("<선택한 음식점 주변 공영주차장 목록>");
					String Query2 = "select distinct pName, free, pAddr " +
					        "from park, kindrestuarant K, location L " +
					        "where K.kAddr = lAddr " +
					        "and K.kname = '" + sel_name + "' " +
					        "and park.longitude between (L.longitude - 0.015) and (L.longitude + 0.015) " +
					        "and park.latitude between (L.latitude - 0.015) and (L.latitude + 0.015);";
					
					pst = connect.prepareStatement(Query2);
					rs = pst.executeQuery();
					
					System.out.println(String.format("%-30s %-30s %-30s", "주차장 이름","무료/유료","주소"));
					while(rs.next()) {
						System.out.println(String.format("%-30s %-30s %-30s ",rs.getString(1),rs.getString(2),rs.getString(3)));
					}
				}
				else {
					System.out.println("종료");
					break;
				}
			}
				
		}catch(SQLException ex) {
			throw ex;
		}finally {
			try {
				if(rs !=null) {
					rs.close();
				}
				if(st !=null) {
					st.close();
				}
				if(connect!=null) {
					connect.close();
				}
			}catch(SQLException ex){
				throw ex;
			}
		}
			
		//if DBMS is clear;
		//if();
			//input safeRestuarant to database
			
		
			//input kindRestuarant to database
			
	}
	public static void parks_insert(Connection connect,Statement st, ResultSet rs ) throws SQLException {
		
		try{
			
			String filePath = "C:/DB_proj_json/서울시 공영주차장 안내 정보.json";

            // BufferedReader를 사용하여 파일 읽기
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8));

            // 파일 내용을 저장할 StringBuilder 생성
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }

            // 파일 읽기 종료
            bufferedReader.close();

			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(result.toString());

			JSONObject SFHB = (JSONObject) jsonObject.get("DESCRIPTION");
			JSONArray row = (JSONArray) jsonObject.get("DATA");
			
			String[] location = new String[2];
			for(int i=0; i<row.size();i++) {
				
				JSONObject data = (JSONObject) row.get(i);
				try {
					String name = data.get("parking_name").toString();
					String free = data.get("pay_nm").toString();
					String addr = data.get("addr").toString();
					if(data.get("lng")==null||data.get("lat")==null) {
						continue;
					}
					float lng = Float.parseFloat(data.get("lng").toString());
					float lat = Float.parseFloat(data.get("lat").toString());
					
					String insert_park = "insert into Park values ('" + name + "','" + free + "'," + lng + "," + lat + ",'" + addr + "');";
					st.executeUpdate(insert_park);
					
				}catch(SQLException ex) {
					throw ex;
				}
			}
        	
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	public static void kindRestuarants_insert(Connection connect,Statement st, ResultSet rs) throws SQLException {
		
		try{
			String filePath = "C:/DB_proj_json/서울시 착한가격업소 현황.json";

            // BufferedReader를 사용하여 파일 읽기
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8));

            // 파일 내용을 저장할 StringBuilder 생성
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }

            // 파일 읽기 종료
            bufferedReader.close();

			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(result.toString());

			JSONObject SFHB = (JSONObject) jsonObject.get("DESCRIPTION");
			JSONArray row = (JSONArray) jsonObject.get("DATA");
			
			String[] location = new String[2];
			for(int i=0; i<row.size();i++) {

				JSONObject data = (JSONObject) row.get(i);
				try {
					
					String addr = data.get("sh_addr").toString();
					getLocation(addr,location);
					String insert_location = "insert into Location values ('" + location[0] + "','" + location[1] + "','" + addr + "');";
					st.executeUpdate(insert_location);
					String kind_num = data.get("induty_code_se").toString();
					
					if(kind_num.equals("001")||kind_num.equals("002")||kind_num.equals("003")) 
					{
						String name = data.get("sh_name").toString();
						String kind = data.get("induty_code_se_name").toString();
						String insert_kindRestuarant = "insert into KindRestuarant values ('" + name + "','"  + kind + "','"  + kind_num + "','" +addr + "');";
						st.executeUpdate(insert_kindRestuarant);
					}
				}catch(SQLException ex) {
					throw ex;
				}
				
			}
        	
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	
	public static void getLocation(String addr, String[] location) throws Exception {

		String address = URLEncoder.encode(addr, "UTF-8");

		String url = "https://dapi.kakao.com/v2/local/search/address.json?query=" + address;

		String jsonString = new String();

		String buf;

		URL Url = new URL(url);

		HttpsURLConnection conn = (HttpsURLConnection) Url.openConnection();
		String auth = "KakaoAK " + "0832119a8a00b2f9c28f1efcef371287";
		conn.setRequestMethod("GET");
		conn.setRequestProperty("X-Requested-With", "curl");
		conn.setRequestProperty("Authorization", auth);

		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
		while ((buf = br.readLine()) != null) {
			jsonString += buf;
		}
		JSONParser paser = new JSONParser();

		JSONObject J = (JSONObject) paser.parse(jsonString);
		JSONObject meta = (JSONObject) J.get("meta");

		JSONArray data = (JSONArray) J.get("documents");
		long size = (long) meta.get("total_count");
		
		
		if (size > 0) {
			JSONObject jsonX = (JSONObject) data.get(0);
			location[0] = jsonX.get("x").toString();
			location[1] = jsonX.get("y").toString();
		}
	}
}
