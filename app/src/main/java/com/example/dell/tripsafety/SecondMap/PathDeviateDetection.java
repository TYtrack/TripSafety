package com.example.dell.tripsafety.SecondMap;

import android.util.Log;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import java.lang.reflect.Array;
import java.util.Arrays;

public class PathDeviateDetection {
	private GridMap pathGridMap;
	private String []currentPath = null;
	private int lengthOfPath;



	public PathDeviateDetection(GridMap pathGridMap){
		this.pathGridMap = pathGridMap;
		if(!pathGridMap.isEmptyOfPath()) {
			this.currentPath = pathGridMap.getRegPath();
			//对路径序号进行排序
			Arrays.sort(this.currentPath);
			this.lengthOfPath = pathGridMap.getLengthOfPath();
		}
	}

	public void setPathGridMap(GridMap pathGridMap) {
		this.pathGridMap = pathGridMap;
		//更新网格的同时，检测该网格内的路径，若有路径，进行更新
		if(!pathGridMap.isEmptyOfPath()) {
			this.currentPath = pathGridMap.getRegPath();
			//对路径序号进行排序
			Arrays.sort(this.currentPath);
			this.lengthOfPath = pathGridMap.getLengthOfPath();
		}
	}

	//更新路径
	public void setCurrentPath(String[] currentPath){
		this.currentPath = currentPath.clone();
		//对路径序号进行排序
		Arrays.sort(this.currentPath);
		this.lengthOfPath = currentPath.length;
	}

	/**
	 * 位于地图网格内时，检测当前网格最近邻的路径网格
	 * @param currentGridId 当前网格的ID，int类型
	 * @return 检测到路径网格返回该网格的ID，若没有检测到，返回 -1
	 */
	public int searchRouteInGrid(int currentGridId){
		int rowsOfGrid, columnsOfGrid;
		int currentRow, currentColumn;

		rowsOfGrid = this.pathGridMap.getGridRows();
		columnsOfGrid = this.pathGridMap.getGridColumns();

		currentRow = this.pathGridMap.getRowOfGridId(currentGridId);
		currentColumn = this.pathGridMap.getColumnOfGridId(currentGridId);

		int upThres = columnsOfGrid - currentColumn;
		int downThres = currentColumn - 1;
		int leftThres = currentRow - 1;
		int rightThres = rowsOfGrid - currentRow;

		int loopNum = 1;
		while(true){
			if(loopNum <= upThres) {
				for (int n = 0; n <= loopNum; n++) {
					int operateId;
					int searchResult;
					if (n == 0) {
						operateId = currentGridId + loopNum * rowsOfGrid;
						searchResult = Arrays.binarySearch(currentPath, String.valueOf(operateId));

						if (searchResult >= 0)
							return operateId;
					} else {
						if (n > 0 && n <= leftThres) {
							operateId = currentGridId + loopNum * rowsOfGrid - n;
							searchResult = Arrays.binarySearch(currentPath, String.valueOf(operateId));

							if (searchResult >= 0)
								return operateId;
						}
						if (n > 0 && n <= rightThres) {
							operateId = currentGridId + loopNum * rowsOfGrid + n;
							searchResult = Arrays.binarySearch(currentPath, String.valueOf(operateId));

							if (searchResult >= 0)
								return operateId;
						}
					}
				}
			}
//				if (loopNum <= leftThres)
//					upRowLoopMin = currentGridId + loopNum * rowsOfGrid - loopNum;
//				else
//					upRowLoopMin = currentGridId + loopNum * rowsOfGrid - leftThres;
//				if(loopNum <= rightThres)
//					upRowLoopMax = currentGridId + loopNum * rowsOfGrid + loopNum;
//				else
//					upRowLoopMax = currentGridId + loopNum * rowsOfGrid + rightThres;
//
//				for (int operateId = upRowLoopMin; operateId <= upRowLoopMax; operateId++) {
//					int searchResult;
//					searchResult = Arrays.binarySearch(currentPath, String.valueOf(operateId));
//
//					if(searchResult >= 0)
//						return operateId;
//				}
//			}

			if(loopNum <= downThres) {
				for (int n = 0; n <= loopNum; n++) {
					int operateId;
					int searchResult;
					if (n == 0) {
						operateId = currentGridId - loopNum * rowsOfGrid;
						searchResult = Arrays.binarySearch(currentPath, String.valueOf(operateId));

						if (searchResult >= 0)
							return operateId;
					} else {
						if (n > 0 && n <= leftThres) {
							operateId = currentGridId - loopNum * rowsOfGrid - n;
							searchResult = Arrays.binarySearch(currentPath, String.valueOf(operateId));

							if (searchResult >= 0)
								return operateId;
						}
						if (n > 0 && n <= rightThres) {
							operateId = currentGridId - loopNum * rowsOfGrid + n;
							searchResult = Arrays.binarySearch(currentPath, String.valueOf(operateId));

							if (searchResult >= 0)
								return operateId;
						}
					}
				}
			}

//			if(loopNum <= downThres){
//				if (loopNum <= leftThres)
//					downRowLoopMin = currentGridId - loopNum * rowsOfGrid - loopNum;
//				else
//					downRowLoopMin = currentGridId - loopNum * rowsOfGrid - leftThres;
//				if(loopNum <= rightThres)
//					downRowLoopMax = currentGridId - loopNum * rowsOfGrid + loopNum;
//				else
//					downRowLoopMax = currentGridId - loopNum * rowsOfGrid + rightThres;
//
//				for(int operateId = downRowLoopMin;operateId <= downRowLoopMax;operateId++) {
//					int searchResult;
//					searchResult = Arrays.binarySearch(currentPath, String.valueOf(operateId));
//
//					if(searchResult >= 0)
//						return operateId;
//				}
//			}

			if(loopNum <= leftThres) {
				for (int n = 0; n <= loopNum; n++) {
					int operateId;
					int searchResult;
					if (n == 0) {
						operateId = currentGridId - loopNum;
						searchResult = Arrays.binarySearch(currentPath, String.valueOf(operateId));

						if (searchResult >= 0)
							return operateId;
					} else {
						if (n > 0 && n <= upThres - 1) {
							operateId = currentGridId - loopNum + n * rowsOfGrid;
							searchResult = Arrays.binarySearch(currentPath, String.valueOf(operateId));

							if (searchResult >= 0)
								return operateId;
						}
						if (n > 0 && n <= downThres - 1) {
							operateId = currentGridId - loopNum - n * rowsOfGrid;
							searchResult = Arrays.binarySearch(currentPath, String.valueOf(operateId));

							if (searchResult >= 0)
								return operateId;
						}
					}
				}
			}

//			if(loopNum <= leftThres){
//				if(loopNum <= downThres)
//					leftColumnLoopMin = currentGridId - (loopNum - 1) * rowsOfGrid - loopNum;
//				else
//					leftColumnLoopMin = currentGridId - downThres * rowsOfGrid - loopNum;
//				if(loopNum <= upThres)
//					leftColumnLoopMax = currentGridId + (loopNum - 1) * rowsOfGrid - loopNum;
//				else
//					leftColumnLoopMax = currentGridId + upThres * rowsOfGrid - loopNum;
//
//				for(int operateId = leftColumnLoopMin;operateId <= leftColumnLoopMax;operateId += rowsOfGrid ) {
//					int searchResult;
//					searchResult = Arrays.binarySearch(currentPath, String.valueOf(operateId));
//
//					if(searchResult >= 0)
//						return operateId;
//				}
//			}

			if(loopNum <= rightThres) {
				for (int n = 0; n <= loopNum; n++) {
					int operateId;
					int searchResult;
					if (n == 0) {
						operateId = currentGridId + loopNum;
						searchResult = Arrays.binarySearch(currentPath, String.valueOf(operateId));

						if (searchResult >= 0)
							return operateId;
					} else {
						if (n > 0 && n <= upThres - 1) {
							operateId = currentGridId + loopNum + n * rowsOfGrid;
							searchResult = Arrays.binarySearch(currentPath, String.valueOf(operateId));

							if (searchResult >= 0)
								return operateId;
						}
						if (n > 0 && n <= downThres - 1) {
							operateId = currentGridId + loopNum - n * rowsOfGrid;
							searchResult = Arrays.binarySearch(currentPath, String.valueOf(operateId));

							if (searchResult >= 0)
								return operateId;
						}
					}
				}
			}

//			if(loopNum <= rightThres){
//				if(loopNum <= downThres)
//					rightColumnLoopMin = currentGridId - (loopNum - 1) * rowsOfGrid + loopNum;
//				else
//					rightColumnLoopMin = currentGridId - downThres * rowsOfGrid + loopNum;
//				if(loopNum <= upThres)
//					rightColumnLoopMax = currentGridId + (loopNum - 1) * rowsOfGrid + loopNum;
//				else
//					rightColumnLoopMax = currentGridId + upThres * rowsOfGrid + loopNum;
//
//				for(int operateId = rightColumnLoopMin;operateId <= rightColumnLoopMax;operateId += rowsOfGrid ) {
//					int searchResult;
//					searchResult = Arrays.binarySearch(currentPath, String.valueOf(operateId));
//
//					if(searchResult >= 0)
//						return operateId;
//				}
//			}
			if(loopNum > upThres && loopNum > downThres &&
					loopNum > leftThres && loopNum > rightThres)
				break;
			loopNum++;
		}
		return -1;     //-1表示在该网格地图中没有检测出路径
	}

	/**
	 * 位于地图网格内时，检测当前网格最近邻的路径网格
	 * @param currentLocation 当前所在的地理坐标，LatLng类型
	 * @return 检测到路径网格时，返回该网格的ID，若没有检测到，返回 -1
	 */
	public int searchRouteInGrid(LatLng currentLocation){
		double lat,lngt;
		int currentGridId;

		lat = currentLocation.latitude;
		lngt = currentLocation.longitude;
		currentGridId = this.pathGridMap.positionToGrid(lat,lngt);

		return searchRouteInGrid(currentGridId);
	}

	/**
	 * 位于地图网格之外时，检测当前网格最近邻的路径网格
	 * @param currentLocation 当前所在的地理坐标，LatLng类型
	 * @return 检测到路径网格时，返回该网格的ID，若没有检测到，返回 -1
	 */
	public int searchRouteOutGrid(LatLng currentLocation) {
		int fakeGridId;
		MapDirection mapDirection;

		int routeId = -1;

		//获取映射方向
		mapDirection = getCrrentMapDirection(currentLocation);
		Log.d("PGN枚举测试", "deviateDetection: "+mapDirection.name());

		fakeGridId = outerLocationToGrid(currentLocation, mapDirection);
		Log.d("PGNdis", "searchRouteOutGrid: 映射到的网格为："+fakeGridId);

		if(Arrays.binarySearch(currentPath, String.valueOf(fakeGridId)) >= 0)
			routeId = fakeGridId;
		else
			//return searchRouteInGrid(fakeGridId);
			switch (mapDirection){
				case UP:
				case DOWN:
					Log.d("PGN枚举测试", "searchRouteOutGrid: "+mapDirection.name());
					routeId = outerSearchRouteOfUpDown(fakeGridId);
					break;
				case LEFT:
				case RIGHT:
					Log.d("PGN枚举测试", "searchRouteOutGrid: "+mapDirection.name());
					routeId = outerSearchRouteOfLeftRight(fakeGridId);
					break;
			}
			return routeId;
	}

	public double deviateDetection(LatLng currentLocation){
		int routeGridId;
		double distance;
		double [] positions = new double[4];
		double routeLat, routeLngt;
		LatLng routeLatLng ;

		if(this.pathGridMap.isContainPoint(currentLocation.latitude, currentLocation.longitude)){
			routeGridId = searchRouteInGrid(currentLocation);
			positions = this.pathGridMap.gridToPosition(routeGridId);
		}
		else{
			routeGridId = searchRouteOutGrid(currentLocation);
			positions = this.pathGridMap.gridToPosition(routeGridId);
		}

		Log.d("PGNdis", "deviateDetection: 此时搜索到的网格id为："+routeGridId);

		routeLat = (positions[0] + positions[2])/2;
		routeLngt = (positions[1] + positions[3])/2;
		routeLatLng = new LatLng(routeLat, routeLngt);

		Log.d("PGNdis", "deviateDetection: 当前位置为："+currentLocation.toString());
		Log.d("PGNdis", "deviateDetection: 目标点的位置为："+routeLatLng.toString());
		distance = DistanceUtil. getDistance(currentLocation, routeLatLng);

		return distance;
	}

	/**
	 *  将网格地图外部的位置映射到网格地图内部
	 * @param currentLocation 当前的位置，LatLng类型
	 * @return 返回映射到的网格地图的ID，若没有结果，返回 -1
	 */
	private int outerLocationToGrid(LatLng currentLocation, MapDirection direction){
		int fakeGridId = -1;
		double currentLat, currentLngt;
		double minLatOfGrid, maxLatOfGrid;
		double minLngtOfGrid, maxLngtOfGrid;

		int rowsOfGrid, columnsOfGrid;
		double gridSize;
		gridSize = this.pathGridMap.getGridSize();

		currentLat = currentLocation.latitude;
		currentLngt = currentLocation.longitude;

		minLatOfGrid = this.pathGridMap.getMapMinLat();
		maxLatOfGrid = this.pathGridMap.getMapMaxLat();
		minLngtOfGrid = this.pathGridMap.getMapMinLngt();
		maxLngtOfGrid = this.pathGridMap.getMapMaxLngt();

		rowsOfGrid = this.pathGridMap.getGridRows();
		columnsOfGrid = this.pathGridMap.getGridColumns();

		//当位置在网格地图的上方时
		if(currentLat > maxLatOfGrid && currentLngt <= maxLngtOfGrid && currentLngt >= minLngtOfGrid){
			int relativeGridId;
			relativeGridId = getRelativeLngt(currentLngt, minLngtOfGrid, gridSize);
			fakeGridId = rowsOfGrid * (columnsOfGrid - 1) + relativeGridId;
		}
		//当位置在网格地图的下方时
		else if(currentLat < minLatOfGrid && currentLngt <= maxLngtOfGrid && currentLngt >= minLngtOfGrid){
			int relativeGridId;
			relativeGridId = getRelativeLngt(currentLngt, minLngtOfGrid, gridSize);
			fakeGridId = relativeGridId;
		}
		//当位置在网格地图左边时
		else if(currentLngt < minLngtOfGrid && currentLat <= maxLatOfGrid && currentLat >= minLatOfGrid){
			int relativeGridId;
			relativeGridId = getRelativeLat(currentLat, minLatOfGrid, gridSize);
			fakeGridId = (relativeGridId - 1) * rowsOfGrid + 1;
		}
		//当位置在网格地图右边时
		else if(currentLngt > maxLngtOfGrid && currentLat <= maxLatOfGrid && currentLat >= minLatOfGrid){
			int relativeGridId;
			relativeGridId = getRelativeLat(currentLat, minLatOfGrid, gridSize);
			fakeGridId = relativeGridId * rowsOfGrid;
		}
		//当位置位于左上角
		else if(currentLat >= maxLatOfGrid && currentLngt <= minLngtOfGrid){
			fakeGridId = rowsOfGrid * (columnsOfGrid - 1) + 1;
		}
		//当位置位于右上角
		else if(currentLat >= maxLatOfGrid && currentLngt >= maxLngtOfGrid){
			fakeGridId = rowsOfGrid * columnsOfGrid;
		}
		//当位置位于左下角
		else if(currentLat <= minLatOfGrid && currentLngt <= minLngtOfGrid){
			fakeGridId = 1;
		}
		//当位置位于右下角
		else if(currentLat <= maxLatOfGrid && currentLngt >= maxLngtOfGrid){
			fakeGridId = rowsOfGrid;
		}
		return fakeGridId;
	}

	private int getRelativeLngt(double currentLngt, double minLngtOfGrid, double gridSize){
		double gapOfLngt;
		double relativeGridId;

		gapOfLngt = currentLngt - minLngtOfGrid;

		if (gapOfLngt == 0)
			relativeGridId = 1;
			//如果差值能够被网格大小整除，倍数就作为标号，也就是右临界向左取值。
		else if (gapOfLngt % gridSize == 0)
			relativeGridId = gapOfLngt/gridSize;
		else
			relativeGridId = ((gapOfLngt - gapOfLngt % gapOfLngt)/gridSize) + 1;

		return (int)relativeGridId;
	}

	private int getRelativeLat(double currentLat, double minLatOfGrid, double gridSize){
		double gapOfLat;
		double relativeGridId;

		gapOfLat = currentLat - minLatOfGrid;

		if (gapOfLat == 0)
			relativeGridId = 1;
			//如果差值能够被网格大小整除，倍数就作为标号，也就是右临界向左取值。
		else if (gapOfLat % gridSize == 0)
			relativeGridId = gapOfLat/gridSize;
		else
			relativeGridId = ((gapOfLat - gapOfLat % gapOfLat)/gridSize) + 1;

		return (int)relativeGridId;
	}

	/**
	 * 修改了在网格内检索路径的顺序，当外部坐标从上或下面映射到网格地图时，进行调用
	 * 先左右搜索，后上下搜索，
	 * @param currentGridId 当前网格的ID，int类型
	 * @return 检测到路径网格返回该网格的ID，若没有检测到，返回 -1
	 */
	private int outerSearchRouteOfUpDown(int currentGridId){
		int rowsOfGrid, columnsOfGrid;
		int currentRow, currentColumn;

		rowsOfGrid = this.pathGridMap.getGridRows();
		columnsOfGrid = this.pathGridMap.getGridColumns();

		currentRow = this.pathGridMap.getRowOfGridId(currentGridId);
		currentColumn = this.pathGridMap.getColumnOfGridId(currentGridId);

		int upThres = columnsOfGrid - currentColumn;
		int downThres = currentColumn - 1;
		int leftThres = currentRow - 1;
		int rightThres = rowsOfGrid - currentRow;

		int loopNum = 1;
		while(true){
			if(loopNum <= leftThres) {
				for (int n = 0; n <= loopNum; n++) {
					int operateId;
					int searchResult;
					if (n == 0) {
						operateId = currentGridId - loopNum;
						searchResult = Arrays.binarySearch(currentPath, String.valueOf(operateId));

						if (searchResult >= 0)
							return operateId;
					} else {
						if (n > 0 && n <= upThres - 1) {
							operateId = currentGridId - loopNum + n * rowsOfGrid;
							searchResult = Arrays.binarySearch(currentPath, String.valueOf(operateId));

							if (searchResult >= 0)
								return operateId;
						}
						if (n > 0 && n <= downThres - 1) {
							operateId = currentGridId - loopNum - n * rowsOfGrid;
							searchResult = Arrays.binarySearch(currentPath, String.valueOf(operateId));

							if (searchResult >= 0)
								return operateId;
						}
					}
				}
			}

			if(loopNum <= rightThres) {
				for (int n = 0; n <= loopNum; n++) {
					int operateId;
					int searchResult;
					if (n == 0) {
						operateId = currentGridId + loopNum;
						searchResult = Arrays.binarySearch(currentPath, String.valueOf(operateId));

						if (searchResult >= 0)
							return operateId;
					} else {
						if (n > 0 && n <= upThres - 1) {
							operateId = currentGridId + loopNum + n * rowsOfGrid;
							searchResult = Arrays.binarySearch(currentPath, String.valueOf(operateId));

							if (searchResult >= 0)
								return operateId;
						}
						if (n > 0 && n <= downThres - 1) {
							operateId = currentGridId + loopNum - n * rowsOfGrid;
							searchResult = Arrays.binarySearch(currentPath, String.valueOf(operateId));

							if (searchResult >= 0)
								return operateId;
						}
					}
				}
			}

			if(loopNum <= upThres) {
				for (int n = 0; n <= loopNum; n++) {
					int operateId;
					int searchResult;
					if (n == 0) {
						operateId = currentGridId + loopNum * rowsOfGrid;
						searchResult = Arrays.binarySearch(currentPath, String.valueOf(operateId));

						if (searchResult >= 0)
							return operateId;
					} else {
						if (n > 0 && n <= leftThres) {
							operateId = currentGridId + loopNum * rowsOfGrid - n;
							searchResult = Arrays.binarySearch(currentPath, String.valueOf(operateId));

							if (searchResult >= 0)
								return operateId;
						}
						if (n > 0 && n <= rightThres) {
							operateId = currentGridId + loopNum * rowsOfGrid + n;
							searchResult = Arrays.binarySearch(currentPath, String.valueOf(operateId));

							if (searchResult >= 0)
								return operateId;
						}
					}
				}
			}

			if(loopNum <= downThres) {
				for (int n = 0; n <= loopNum; n++) {
					int operateId;
					int searchResult;
					if (n == 0) {
						operateId = currentGridId - loopNum * rowsOfGrid;
						searchResult = Arrays.binarySearch(currentPath, String.valueOf(operateId));

						if (searchResult >= 0)
							return operateId;
					} else {
						if (n > 0 && n <= leftThres) {
							operateId = currentGridId - loopNum * rowsOfGrid - n;
							searchResult = Arrays.binarySearch(currentPath, String.valueOf(operateId));

							if (searchResult >= 0)
								return operateId;
						}
						if (n > 0 && n <= rightThres) {
							operateId = currentGridId - loopNum * rowsOfGrid + n;
							searchResult = Arrays.binarySearch(currentPath, String.valueOf(operateId));

							if (searchResult >= 0)
								return operateId;
						}
					}
				}
			}

			if(loopNum > upThres && loopNum > downThres &&
					loopNum > leftThres && loopNum > rightThres)
				break;
			loopNum++;
		}
		return -1;     //-1表示在该网格地图中没有检测出路径
	}

	/**
	 * 当外部坐标从左或右面映射到网格地图时，进行调用
	 * 先上下搜索，后左右搜索，与默认搜索方式一致，故不做修改
	 * @param currentGridId 当前网格的ID，int类型
	 * @return 检测到路径网格返回该网格的ID，若没有检测到，返回 -1
	 */
	private int outerSearchRouteOfLeftRight(int currentGridId)
	{
		return searchRouteInGrid(currentGridId);
	}

	private enum MapDirection {
		UP, DOWN, LEFT, RIGHT
	}

	private MapDirection getCrrentMapDirection(LatLng currentLocation){
		double currentLat, currentLngt;
		double minLatOfGrid, maxLatOfGrid;
		double minLngtOfGrid, maxLngtOfGrid;

		MapDirection mapDirection;
		mapDirection = MapDirection.UP;

		currentLat = currentLocation.latitude;
		currentLngt = currentLocation.longitude;

		minLatOfGrid = this.pathGridMap.getMapMinLat();
		maxLatOfGrid = this.pathGridMap.getMapMaxLat();
		minLngtOfGrid = this.pathGridMap.getMapMinLngt();
		maxLngtOfGrid = this.pathGridMap.getMapMaxLngt();

		//当位置在网格地图的上方时
		if(currentLat > maxLatOfGrid && currentLngt <= maxLngtOfGrid && currentLngt >= minLngtOfGrid){
			mapDirection = MapDirection.UP;
		}
		//当位置在网格地图的下方时
		else if(currentLat < minLatOfGrid && currentLngt <= maxLngtOfGrid && currentLngt >= minLngtOfGrid){

			mapDirection = MapDirection.DOWN;
		}
		//当位置在网格地图左边时
		else if(currentLngt < minLngtOfGrid && currentLat <= maxLatOfGrid && currentLat >= minLatOfGrid){

			mapDirection = MapDirection.LEFT;
		}
		//当位置在网格地图右边时
		else if(currentLngt > maxLngtOfGrid && currentLat <= maxLatOfGrid && currentLat >= minLatOfGrid){
			mapDirection = MapDirection.RIGHT;
		}
		return mapDirection;
	}
}
