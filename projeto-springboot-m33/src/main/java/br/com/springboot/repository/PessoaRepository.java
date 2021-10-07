package br.com.springboot.repository;

import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.springboot.model.Pessoa;

@Repository
@Transactional
public interface PessoaRepository extends JpaRepository<Pessoa, Long>{

	@Query("select p from Pessoa p where p.nome like %?1%")
	public List<Pessoa> buscarPorNome(String nome);

	@Query("select p from Pessoa p where p.nome like %?1% and p.sexo = ?2")
	public List<Pessoa> buscarPorNomeESexo(String nome, String sexo);

	// Retornar uma lista de pessoas de acordo com o nome pesquisado
	default Page<Pessoa> procurarPessoaPorNomePage(String nome, Pageable pageable){
		
		Pessoa pessoa = new Pessoa();
		pessoa.setNome(nome);
		
		/*Estamos configurando a pesquisa para consultar por partes do nome no Banco de Dados*/
		/* Igual ao LIKE do SQL */
		ExampleMatcher exampleMatcher = ExampleMatcher.matchingAny().withMatcher("nome", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
		
		/* Une o objeto com o valor e a configuração para consultar no Banco de Dados */
		Example<Pessoa> example = Example.of(pessoa, exampleMatcher);
		
		Page<Pessoa> pessoas = findAll(example, pageable);
		
		return pessoas;
	}
	
}
