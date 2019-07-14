package com.example.dell.tripsafety.SecondMap;

/**
 * Created by DELL on 2019/2/27.
 */

public class GridMap {
    private double minLat;
    private double minLngt;
    private double maxLat;
    private double maxLngt;
    private double gridSize;

    private int rows;//每行网格的个数,由经度差决定
    private int columns;//每列网格的个数,由纬度差决定

    private String []regPath = null;   //设置接口未实现
    private int lengthOfPath = 0;   //设置接口未实现

    public GridMap(double minLat, double minLngt, double maxLat, double maxLngt, int gridSize)
    {
        this.minLat = minLat;
        this.minLngt = minLngt;
        this.maxLat = maxLat;
        this.maxLngt = maxLngt;
        this.gridSize = (double)gridSize/3600.0;

        //计算网格的行网格个数和列网格个数
        //行网格数由经度差决定
        //列网格数由纬度差决定
        double gap_lat = maxLat - minLat;
        double gap_lngt = maxLngt - minLngt;
        if(gap_lat % this.gridSize == 0)
            this.rows = (int)(gap_lngt/this.gridSize);
        else
            this.rows = (int)(gap_lngt/this.gridSize)+1;
        if(gap_lngt % this.gridSize == 0)
            this.columns = (int)(gap_lat/this.gridSize);
        else
            this.columns = (int)(gap_lat/this.gridSize)+1;
    }

    /**
     * 返回该网格总的区域范围
     * @return 返回该网格总的区域范围，是一个长度为 4 的数组，[最小纬度，最小经度，最大纬度，最大经度]
     */
    public double[] getPositionRange()
    {
        double positions[] = new double[4];
        positions[0] = this.minLat;
        positions[1] = this.minLngt;
        positions[2] = this.maxLat;
        positions[3] = this.maxLngt;
        return positions;
    }

    public double getGridSize()
    {
        return this.gridSize;
    }

    public int getGridRows()
    {
        return this.rows;
    }

    public int getGridColumns()
    {
        return this.columns;
    }

    public int getGridNums()
    {
        return this.rows * this.columns;
    }

    public double getMapMaxLat()
    {
        return this.maxLat;
    }

    public double getMapMaxLngt()
    {
        return this.maxLngt;
    }

    public double getMapMinLat()
    {
        return this.minLat;
    }

    public double getMapMinLngt()
    {
        return this.minLngt;
    }

    public String[] getRegPath(){return this.regPath;}

    public int getLengthOfPath(){return this.lengthOfPath;}

    public boolean isEmptyOfPath()
    {
    	if(this.regPath == null){
    		return true;
	    }
	    else
	    	return false;
    }


    /**
     * 判断某点位置是否位于网格地图内
     * @param lat 纬度
     * @param lngt 经度
     * @return 位于网格地图返回true，否则返回false
     */
    public boolean isContainPoint(double lat, double lngt)
    {
        if(lat >= this.minLat && lat <= this.maxLat
                && lngt >= this.minLngt && lngt <= this.maxLngt)
        {
            return true;
        }
        return false;
    }

    public int positionToGrid(double lat, double lngt)
    {
    	if(!isContainPoint(lat, lngt)){
    		return -1;
	    }
	    else{
	        int row_id;
	        int column_id;
	        double gridSize = this.gridSize;

	        double gap_lat;
	        double gap_lngt;

	        int gridId;

	        gap_lngt = lngt - minLngt;
	        if (gap_lngt == 0)
	            row_id = 1;
	        else if (gap_lngt % gridSize == 0)
	            row_id = (int)(gap_lngt/gridSize);
	        else
	            row_id = (int)(gap_lngt /gridSize) + 1;

	        gap_lat = lat - this.minLat;
	        if (gap_lat == 0)
	            column_id = 1;
	        else if (gap_lat % gridSize == 0)
	            column_id = (int)(gap_lat/gridSize);
	        else
	            column_id = (int)(gap_lat/gridSize) + 1;

	        gridId = ((column_id - 1)*this.rows) + row_id;
	        return gridId;
    	}
    }

	/**
	 * 根据输入的网格编号，获得该网格在行上面的坐标，即通俗意义上的第几列
	 * @param gridId
	 * @return 获得该网格在行上面的坐标，即通俗意义上的第几列
	 */
	public int getRowOfGridId(int gridId){
		int row_id;

		row_id = gridId % this.rows;

		if (row_id == 0)
			row_id = this.rows;

		return row_id;
    }

	/**
	 * 根据输入的网格编号，获得该网格在列上面的坐标，即通俗意义上的第几行
	 * @param gridId
	 * @return 获得该网格在列上面的坐标，即通俗意义上的第几行
	 */
	public int getColumnOfGridId(int gridId){
		int row_id;
		int column_id;

		row_id = gridId % this.rows;
		if (row_id == 0)
			column_id = gridId/this.rows;
		else
			column_id = (gridId - row_id)/this.rows + 1;

		return column_id;
	}

    /**
     * 根据输入的网格编号，获得该网格在地图上的具体位置
     * @param gridId
     * @return 长度为 4 的数组，[最小纬度，最小经度，最大纬度，最大经度]
     */
    public double [] gridToPosition(int gridId)
    {
        double [] positions ; //用来储存两对经纬度

        int row_id;
        int column_id;

        row_id = gridId % this.rows;
        if (row_id == 0)
            column_id = gridId/this.rows;
        else
            column_id = (gridId - row_id)/this.rows + 1;

        if (row_id == 0)
            row_id = this.rows;


        double min_lat;
        double min_lngt;
        double max_lat;
        double max_lngt;
        min_lat = this.minLat + (column_id - 1)*this.gridSize;
        min_lngt = this.minLngt + (row_id-1)*this.gridSize;

        //计算网格的最大边界
        if(row_id == this.rows)
            max_lngt = this.maxLngt;
        else
            max_lngt = min_lngt + this.gridSize;
        if(column_id == this.columns)
            max_lat = this.maxLat;
        else
            max_lat = min_lat + this.gridSize;

        //将网格经纬度放入数组中
        positions = new double []{min_lat, min_lngt, max_lat, max_lngt};

        return positions;
    }
}
