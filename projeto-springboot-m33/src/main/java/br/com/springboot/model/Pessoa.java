package br.com.springboot.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class Pessoa implements Serializable{
	private static final long serialVersionUID = 1L;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@NotEmpty(message = "Preencha o nome corretamente!")
	@NotNull(message = "Preencha o nome corretamente!")
	private String nome;
	
	@NotEmpty(message = "Preencha o Sobrenome corretamente!")
	@NotNull(message = "Preencha o Sobrenome corretamente!")
	private String sobrenome;
	
	@Min(value = 18, message = "Idade Inválida")
	private int idade;
	
	@ManyToOne
	private Profissao profissao;
	
	@Enumerated(EnumType.STRING)
	private Hobby hobby;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Temporal(TemporalType.DATE)
	private Date dataNascimento;
	
	@Lob
	private byte[] curriculo;
	
	private String nomeArquivoCurriculo;
	private String tipoArquivoCurriculo;
	
	private String sexo;
	
	private String cep;
	private String rua;
	private String bairro;
	private String cidade;
	private String uf;
	
	// orphanRemoval = true; cascade = CascadeType.ALL => atributos para remover em cascada por causa da tabela telefone
	@OneToMany(mappedBy = "pessoa", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<Telefone> telefones;
	
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getSobrenome() {
		return sobrenome;
	}
	
	public void setSobrenome(String sobrenome) {
		this.sobrenome = sobrenome;
	}
	
	public int getIdade() {
		return idade;
	}
	
	public void setIdade(int idade) {
		this.idade = idade;
	}
	
	public Profissao getProfissao() {
		return profissao;
	}

	public void setProfissao(Profissao profissao) {
		this.profissao = profissao;
	}

	public Hobby getHobby() {
		return hobby;
	}

	public void setHobby(Hobby hobby) {
		this.hobby = hobby;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}
	
	public byte[] getCurriculo() {
		return curriculo;
	}

	public void setCurriculo(byte[] curriculo) {
		this.curriculo = curriculo;
	}

	public String getNomeArquivoCurriculo() {
		return nomeArquivoCurriculo;
	}

	public void setNomeArquivoCurriculo(String nomeArquivoCurriculo) {
		this.nomeArquivoCurriculo = nomeArquivoCurriculo;
	}

	public String getTipoArquivoCurriculo() {
		return tipoArquivoCurriculo;
	}

	public void setTipoArquivoCurriculo(String tipoArquivoCurriculo) {
		this.tipoArquivoCurriculo = tipoArquivoCurriculo;
	}

	public String getSexo() {
		return sexo;
	}
	
	public void setSexo(String sexo) {
		this.sexo = sexo;
	}
	
	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getRua() {
		return rua;
	}

	public void setRua(String rua) {
		this.rua = rua;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	public List<Telefone> getTelefones() {
		return telefones;
	}
	
	public void setTelefones(List<Telefone> telefones) {
		this.telefones = telefones;
	}
	
}
