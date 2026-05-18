/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.SIGECA.service;

import java.util.List;

/**
 *
 * @author crist
 */
public interface CRUD<T> {
    public void save(T t);
    public void delete(int i);
    public List<T> getAll();
    public T getById(int i);
}
