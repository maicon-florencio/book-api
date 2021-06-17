package com.udemy.api.library;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class teste {
    int num1 =10, num2=5;
    Calculadora cal;

    @BeforeEach
    public void setUp(){
         cal = new Calculadora();
    }
    @Test
    public void estruturaTeste(){
        
       int res = cal.somar(num1, num2);
       Assertions.assertThat(res).isEqualTo(15);
 
    }
    @Test
    public void primeiroMock(){
        List<String> lista = Mockito.mock(ArrayList.class);
        
    Mockito.when(lista.size()).thenReturn(20);
        
        int size = lista.size();
        Assertions.assertThat(size).isEqualTo(20);

    }

}

class Calculadora{
    int somar(int num1,int num2){
        if(num1 < 0 || num2 < 0){
        throw new RuntimeException("Ferrou man, negativo");
    }
        return num1 + num2;
    }
}


