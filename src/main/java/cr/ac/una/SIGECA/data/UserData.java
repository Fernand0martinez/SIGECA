/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.SIGECA.data;

import cr.ac.una.SIGECA.domain.User;

/**
 *
 * @author crist
 */
public class UserData {
    public static User user;
    
    public UserData(){
    }
    
    public static void clear(){
        user=null;
    }
    
    public static User getUser(){
        return user;
    }
}
