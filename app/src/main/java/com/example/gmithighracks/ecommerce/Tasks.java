package com.example.gmithighracks.ecommerce;

import java.security.Timestamp;

/**
 * Created by gmithighracks on 9/18/15.
 */
public class Tasks {

    private String name,sDescription,fDescription,created_by;
    private String stime,etime;
    private int salary,closed;


    public Tasks (String n, String sDes,String fDes,String stim,String etim,int sal,int clo,String create)
    {
        this.name = n;
        this.sDescription = sDes;
        this.fDescription = fDes;
        this.stime = stim;
        this.etime = etim;
        this.salary = sal;
        this.closed = clo;
        this.created_by = create;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getfDescription() {
        return fDescription;
    }

    public void setfDescription(String fDescription) {
        this.fDescription = fDescription;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getStime() {
        return stime;
    }

    public void setStime(String stime) {
        this.stime = stime;
    }

    public String getEtime() {
        return etime;
    }

    public void setEtime(String etime) {
        this.etime = etime;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public int getClosed() {
        return closed;
    }

    public void setClosed(int closed) {
        this.closed = closed;
    }

    public String getsDescription() {

        return sDescription;
    }

    public void setsDescription(String sDescription) {
        this.sDescription = sDescription;
    }
}
