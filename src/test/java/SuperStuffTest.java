/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.cosw.superstuff.logica.SuperStuffLogica;
import com.cosw.superstuff.persistencia.Categoria;
import com.cosw.superstuff.persistencia.Descuento;
import com.cosw.superstuff.persistencia.Lugar;
import com.cosw.superstuff.persistencia.Pais;
import com.cosw.superstuff.persistencia.Producto;
import com.cosw.superstuff.persistencia.Proveedor;
import com.cosw.superstuff.rep.RepositorioCategorias;
import com.cosw.superstuff.rep.RepositorioDescuentos;
import com.cosw.superstuff.rep.RepositorioLugares;
import com.cosw.superstuff.rep.RepositorioPaises;
import com.cosw.superstuff.rep.RepositorioProductos;
import com.cosw.superstuff.rep.RepositorioProveedores;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author HOMERO
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContextH2.xml"})
public class SuperStuffTest {
    
    @Autowired
    private SuperStuffLogica superStuff;
    
    @Autowired
    private RepositorioLugares repositorioLugares;
    
    @Autowired
    private RepositorioPaises repositorioPaises;
    
    @Autowired
    private RepositorioProveedores repositorioProveedores;
    
    @Autowired
    private RepositorioDescuentos repositorioDescuentos;
    
    @Autowired
    private RepositorioCategorias repositorioCategorias;
    
    @Autowired
    private RepositorioProductos repositorioProductos;
    
    private static boolean DATOSPREPARADOS = false;
    
    public SuperStuffTest() {
    }
    
    @Before
    public void setUp() {
        if(!DATOSPREPARADOS) {
            Pais p1 = new Pais("Colombia", "COL", "ESPAÃ‘OL", Pais.SIHAYCOBERTURA);
            Set<Lugar> newPlaces = new LinkedHashSet<>();
            newPlaces.add(new Lugar(p1, "BogotÃ¡", "Cedritos"));
            p1.setLugares(newPlaces);
            repositorioPaises.save(p1);
            repositorioPaises.save(new Pais("Francia", "PA", "FR", Pais.SIHAYCOBERTURA));
            
            repositorioCategorias.save(new Categoria(1, "Frutas", "Categoria que agrupa las frutas"));
            repositorioCategorias.save(new Categoria(100, "Alcohol", "Categoria que agrupa Bebidas Alcoholicas"));
            repositorioDescuentos.save(new Descuento(0, new Date(), new Date(), "Esto es un descuento del 0%"));
            repositorioDescuentos.save(new Descuento(100, new Date(), new Date(), "Esto es un descuento del 10%"));
            
            List<Lugar> lugares = (List<Lugar>)repositorioLugares.findAll();
            superStuff.crearNuevoProveedor(new Proveedor(1, lugares.get(0), "Licorera El Tio Moe", "Calle Falsa 123", "3044463405", "www.eltiomoe.com", "eltiomoe@mail.com"));
            superStuff.crearNuevoProveedor(new Proveedor(2, lugares.get(0), "Jabones Mr. Chispa", "Calle Falsa 121", "3044463404", "www.mrchispa.com", "mrchispa@mail.com"));
            superStuff.crearNuevoProveedor(new Proveedor(3, lugares.get(0), "Alpina", "Calle Falsa 124", "3045463402", "www.prueba.com", "prueba@mail.com"));
            superStuff.crearNuevoProveedor(new Proveedor(4, lugares.get(0), "Ramo", "Calle Falsa 125", "3045463403", "www.prueba.com", "prueba@mail.com"));
            superStuff.crearNuevoProveedor(new Proveedor(5, lugares.get(0), "Telas ECI", "Calle Falsa 126", "3045463404", "www.prueba.com", "prueba@mail.com"));
            superStuff.crearNuevoProveedor(new Proveedor(6, lugares.get(0), "Lacteos ECI", "Calle Falsa 127", "3045463405", "www.prueba.com", "prueba@mail.com"));     
        }
        DATOSPREPARADOS = true;
    }
    
    @After
    public void tearDown() {
    }
    
    /**
     * 
     */
    @Test
    public void crearNuevoProveedorTest(){
        List<Proveedor> proveedores = superStuff.cargarTodosLosProveedores();
        assertEquals("La clase logica ha cargado 6 proveedores?", 6, proveedores.size());
    }
    
    @Test
    public void registrarProductoTest(){                
        Proveedor p = repositorioProveedores.findOne(1);
        Categoria c1 = repositorioCategorias.findOne(100);
        Iterable<Descuento> d1 = repositorioDescuentos.findAll();
        superStuff.registrarProducto(new Producto(1, c1, d1.iterator().next(), "Jack Daniel´s Whiskey Old Time", p, 1000000));
        Producto producto = repositorioProductos.findOne(1);
        
        assertEquals("Ha cargado la descripcion del producto?", "Jack Daniel´s Whiskey Old Time", producto.getDescripcion());
        assertNotNull("La categoria del producto no debe ser nula", producto.getCategoria());
        assertNotNull("El descuento del producto no debe ser nulo", producto.getDescuentos());
        assertNotNull("El proveedor del producto no debe ser nulo", producto.getProveedores());
        assertEquals("El precio del producto es 1000000?", 1000000, producto.getPrecioLista());
    }
    
    @Test
    public void cargarProductosPorProveedorTest(){
        boolean prueba = true;
        List<Lugar> lugares = (List<Lugar>)repositorioLugares.findAll();
        superStuff.crearNuevoProveedor(new Proveedor(1, lugares.get(0), "Licorera El Tio Moe", "Calle Falsa 123", "3044463405", "www.eltiomoe.com", "eltiomoe@mail.com"));
        superStuff.crearNuevoProveedor(new Proveedor(2, lugares.get(0), "Jabones Mr. Chispa", "Calle Falsa 121", "3044463404", "www.mrchispa.com", "mrchispa@mail.com"));
        superStuff.crearNuevoProveedor(new Proveedor(3, lugares.get(0), "Alpina", "Calle Falsa 124", "3045463402", "www.prueba.com", "prueba@mail.com"));
        superStuff.crearNuevoProveedor(new Proveedor(4, lugares.get(0), "Ramo", "Calle Falsa 125", "3045463403", "www.prueba.com", "prueba@mail.com"));
        superStuff.crearNuevoProveedor(new Proveedor(5, lugares.get(0), "Telas ECI", "Calle Falsa 126", "3045463404", "www.prueba.com", "prueba@mail.com"));
        superStuff.crearNuevoProveedor(new Proveedor(6, lugares.get(0), "Lacteos ECI", "Calle Falsa 127", "3045463405", "www.prueba.com", "prueba@mail.com"));
        
        Categoria c = new Categoria(1, "Prueba", "Categoria de prueba");
        Categoria c1 = new Categoria(100, "Bebidas Alcoholicas", "Categoria que agrupa Bebidas Alcoholicas");
        Descuento d = new Descuento(0, new Date(), new Date(), "Esto es un descuento del 0%");
        Descuento d1 = new Descuento(100, new Date(), new Date(), "Esto es un descuento del 10%");
        
        Proveedor p = repositorioProveedores.findOne(1);
        Proveedor pr2 = repositorioProveedores.findOne(2);
        Proveedor pr3 = repositorioProveedores.findOne(3);
        Proveedor pr4 = repositorioProveedores.findOne(4);
        Proveedor pr5 = repositorioProveedores.findOne(5);
        Proveedor pr6 = repositorioProveedores.findOne(6);
        
        repositorioCategorias.save(c);
        repositorioCategorias.save(c1);
        repositorioDescuentos.save(d);
        repositorioDescuentos.save(d1);
        repositorioProductos.save(new Producto(1, c1, d1, "Jack Daniel´s Whiskey Old Time", p, 1000000));
        repositorioProductos.save(new Producto(2, c1, d1, "Cerveza Aguila", p, 1000000));
        repositorioProductos.save(new Producto(3, c1, d1, "Aguardiente Antioqueño", p, 1000000));
        repositorioProductos.save(new Producto(4, c1, d1, "Vino Cariñoso", p, 1000000));
        repositorioProductos.save(new Producto(5, c1, d1, "Aguardiente Blanco del Valle Ice", p, 1000000));
        repositorioProductos.save(new Producto(6, c1, d1, "Baileys Irish Cream", p, 1000000));
        repositorioProductos.save(new Producto(7, c1, d1, "A", pr2, 1000000));
        repositorioProductos.save(new Producto(8, c1, d1, "B", pr3, 1000000));
        repositorioProductos.save(new Producto(9, c1, d1, "C", pr4, 1000000));
        repositorioProductos.save(new Producto(10, c1, d1, "D", pr5, 1000000));
        repositorioProductos.save(new Producto(11, c1, d1, "E", pr6, 1000000));
        repositorioProductos.save(new Producto(12, c1, d1, "F", pr6, 1000000));
        
        List<Producto> producto = superStuff.cargarProductosPorProveedor(1);
        
        for (Producto producto1 : producto) {
            if(producto1.getProveedores().getIdProveedores() != 1){
                prueba = false;
            }
        }assertTrue("Algun producto tiene un proveedor disntinto al solicitado", prueba);
    }
    
    @Test
    public void crearNuevoTenderoTest(){
        
    }
    
    @Test
    public void registrarPedido() {
        
    }
}
