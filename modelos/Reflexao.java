package modelos;

import algebra.*;

import java.util.List;

//Preciso criar Umaa maneira de mudar a reflexão do raio baseado no Reflexão...
//Todo objeto vai ter Uma Reflexão que define Umaa reflexão para determinado ponto de colisão
//Dado Uma raio r ele vai gerar n raios ri
//Reflexão especular gera Uma raio refletido
//Criar Umaa classe Reflexão, todo objeto tem Uma Reflexão
//Para determinado ponto, Uma Reflexão retorna Umaa série de raios
//Uma Reflexão especular teria Uma índice de especularidade, ou seja, quanto ele reflete de luz
//Uma Reflexão glossy similarmente teria outro coeficiente
//Uma Reflexão translucido também
//Uma Reflexão de difusão monte-carlo teria também Uma coeficiente para cada raio refletido
//esse ultimo teria Uma calculo inicial com n raios para determinar a distribuição de probabilidades
//Melhor deixar para implementar como Uma raytracer o montecarlo

//Objeto => n materiais cada qual com Umaa razão para sua reflexão

//Logo, Uma Reflexão tem Umaa razão de eficiência para geração de raios

public abstract class Reflexao {

    int pixelCount;

    double eficiencia;

    abstract void refletir(Ponto ponto, Raio raio, List<Raio> raios);
}

class Especular extends Reflexao{

    void refletir(Ponto ponto, Raio raio, List<Raio> raios){

        Raio refletido = raio.reflexao(ponto);

        refletido.intensidade.vezes(eficiencia);

        raios.add(raio);
    }

    public Raio reflexao(Ponto ponto, Raio raio){

        Raio refletido = new Raio(raio.profundidade+1);
    
        refletido.linha  = raio.linha;
        refletido.coluna = raio.coluna;
        
    
        //Direção muda para o raio refletido no ponto
        refletido.direcao =  raio.direcao
                                  .menos(
                                      ponto.normal
                                      .vezes(
                                        2*raio.direcao
                                        .escalar(ponto.normal)
                                      )
                                  )
                                  .unitario();
    
        //O raio refletido tem origem no ponto de colisão
        refletido.origem = ponto.pos.mais(refletido.direcao);
    
        //Intensidade diminui pelo fator de reflexão especular
        refletido.intensidade = raio.intensidade.mult(ponto.getKe());
    
        return refletido;
      }
}

class Glossy extends Reflexao{

    double roughness;

    void refletir(Ponto ponto, Raio raio, List<Raio> raios){

        Raio refletido = raio.reflexao(ponto);

        refletido.intensidade.vezes(eficiencia);

        raios.add(raio);
    }

    public Raio reflexao(Ponto ponto, Raio raio) {

        Raio refletido = new Raio(raio.profundidade+1);
        refletido.linha = raio.linha;
        refletido.coluna = raio.coluna;
    
        // Direction of ideal reflection
        Vetor reflexaoIdeal = raio.direcao.menos(
            ponto.normal.vezes(2 * raio.direcao.escalar(ponto.normal))
        ).unitario();
    
        // Randomly perturb the reflected direction around the ideal reflection direction
        double theta = Math.acos(Math.pow(Math.random(), 1.0 / (roughness + 1)));
        double phi = 2 * Math.PI * Math.random();
        Vetor reflexaoPerturbada = new Vetor(
            Math.sin(theta) * Math.cos(phi),
            Math.sin(theta) * Math.sin(phi),
            Math.cos(theta)
        );
        Vetor reflexaoFinal = reflexaoPerturbada.mais(reflexaoIdeal).unitario();
    
        // Reflected ray has origin at collision point
        refletido.origem = ponto.pos.mais(reflexaoFinal);
    
        // Reflected ray has direction of reflected vector
        refletido.direcao = reflexaoFinal;
    
        // Intensity is reduced by the specular reflection factor
        refletido.intensidade = raio.intensidade.mult(ponto.getKe());
    
        return refletido;
    }
    
}