package br.com.springboot.model;

public enum Hobby {

	VIAJAR("Viajar"),
	ASSISTIR("Assistir"),
	LER("Ler"),
	PRATICAR_ESPORTES("Praticar Esportes"),
	OUTRO("Outro");	

	private String nome;
	
	private Hobby(String nome) {
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	@Override
	public String toString() {
		return this.name();
	}

}
