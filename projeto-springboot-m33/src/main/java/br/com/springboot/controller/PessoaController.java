package br.com.springboot.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import br.com.br.springboot.service.ReportUtil;
import br.com.springboot.model.Pessoa;
import br.com.springboot.model.Profissao;
import br.com.springboot.model.Telefone;
import br.com.springboot.repository.PessoaRepository;
import br.com.springboot.repository.ProfissaoRepository;
import br.com.springboot.repository.TelefoneRepository;

@Controller
public class PessoaController {

	@Autowired(required = true)
	PessoaRepository pessoaRepository;
	
	@Autowired(required = true)
	TelefoneRepository telefoneRepository;
	
	@Autowired(required = true)
	ReportUtil reportUtil;
	
	@Autowired
	ProfissaoRepository profissaoRepository;
	
	@GetMapping(value = "/cadastropessoa")
	public ModelAndView inicio() {
		
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		modelAndView.addObject("pessoa", new Pessoa());
		
		// Carregando 5 objetos de Pessoa por vez e ordenando pelo atributo "nome" da classe Pessoa
		// findAll retorna um objeto Page de entidades
		modelAndView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5,Sort.by("nome"))));
		
		//carregando profissões do banco de dados para que esteja disponível na tela de Cadastro
		modelAndView.addObject("profissoes", profissaoRepository.findAll());
		
		return modelAndView;
	}
	
	//Método de paginação
	@GetMapping("/pessoaspag")
	public ModelAndView carregarPessoasPorPaginacao(@PageableDefault(size = 5) Pageable pageable, ModelAndView modelAndView ) {
		
		Page<Pessoa> pagePessoa = pessoaRepository.findAll(pageable);
		modelAndView.addObject("pessoas", pagePessoa);
		
		// objeto formulario cadastro
		modelAndView.addObject("pessoa", new Pessoa());
		
		modelAndView.setViewName("cadastro/cadastropessoa");
		
		return modelAndView;
		
	}
	
	//Método de paginação para o resultado de pesquisar pessoas
	@GetMapping("/pesquisarpessoaspag")
	public ModelAndView carregarPessoasPorPaginacaoPesquisadas(@PageableDefault(size = 5) Pageable pageable, ModelAndView modelAndView,
																@RequestParam("nomepesquisa") String nomepesquisa ) {
		
		Page<Pessoa> pagePessoa = pessoaRepository.procurarPessoaPorNomePage(nomepesquisa, pageable);
		modelAndView.addObject("pessoasEncontradasPorNomeInformado", pagePessoa);
		
		modelAndView.addObject("nomepesquisa", nomepesquisa);
		
		modelAndView.setViewName("cadastro/pesquisar-pessoas-por-nome");
		
		return modelAndView;
		
	}

	
	// @Valid => anotação para aplicar as validações feitas na entidade pessoa
	// BindingResult => para retornar as mesagens de erros das validações
	// consumes = {"multipart/form-data"} e final MultipartFile arquivo => para fazer o upload de imagens
	@PostMapping(value = "**/salvarpessoa", consumes = {"multipart/form-data"})
	public ModelAndView salvar(@Valid Pessoa pessoa, BindingResult bindingResult, final MultipartFile arquivo) throws IOException {
		
		pessoa.setTelefones(telefoneRepository.getTelefones(pessoa.getId()));
		
		// verificar se existe error
		if(bindingResult.hasErrors()) {
			ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
			
			modelAndView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
			
			// retorna o objeto que está vindo da VIew com erro para a tela novamente
			modelAndView.addObject("pessoa", pessoa);
			
			// pegar as mensagens de erros do bindingResult para mostrar na tela
			List<String> mensagens = new ArrayList<String>();
			for(ObjectError objectError : bindingResult.getAllErrors()) {
				mensagens.add(objectError.getDefaultMessage());
			}
			
			modelAndView.addObject("mensagens", mensagens);
			
			return modelAndView;
		}
		
		//upload curriculo
		if(arquivo.getSize() > 0) {
			
			pessoa.setCurriculo(arquivo.getBytes()); // salvando pessoa pela primeira vez, então não existe arquivo associado
			
			//pegando o nome original do arquivo passado e o tipo do arquivo (.pdf, .png)
			// para usar no download do arquivo
			pessoa.setNomeArquivoCurriculo(arquivo.getOriginalFilename());
			pessoa.setTipoArquivoCurriculo(arquivo.getContentType());
			
		}else if(pessoa.getId() != null && pessoa.getId() > 0) { // editando pessoa, então tem que pegar o arquivo já existente dessa pessoa
			
			Pessoa pessoaTemp = pessoaRepository.findById(pessoa.getId()).get();
			
			pessoa.setCurriculo(pessoaTemp.getCurriculo());
			pessoa.setNomeArquivoCurriculo(pessoaTemp.getNomeArquivoCurriculo());
			pessoa.setTipoArquivoCurriculo(pessoaTemp.getTipoArquivoCurriculo());
			
		}
		
		pessoaRepository.save(pessoa);
		
		// carregar lista de pessoas para exibir após salvar o novo usuário
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		Iterable<Pessoa> listaPessoas = pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome")));
		modelAndView.addObject("pessoas", listaPessoas);
		modelAndView.addObject("pessoa", new Pessoa());
		
		return modelAndView;
	}
	
	@GetMapping(value = {"**/listarpessoas","/editarpessoa/listarpessoas"})
	public ModelAndView getPessoas() {
		
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
	
		modelAndView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
		modelAndView.addObject("pessoa", new Pessoa());
		
		//carregando profissões do banco de dados para que esteja disponível na tela de Cadastro
		modelAndView.addObject("profissoes", profissaoRepository.findAll());
		
		return modelAndView;
		
	}
	
	@GetMapping(value = "editarpessoa/{idpessoa}")
	public ModelAndView editar(@PathVariable("idpessoa") Long idpessoa) {
		
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		
		Pessoa pessoa = pessoaRepository.findById(idpessoa).get();
		
		modelAndView.addObject("pessoa", pessoa);
	
		//carregando profissões do banco de dados para que esteja disponível na tela de Cadastro
		modelAndView.addObject("profissoes", profissaoRepository.findAll());
		
		return modelAndView;
	}
	
	@GetMapping(value = "excluirpessoa/{idpessoa}")
	public ModelAndView excluir(@PathVariable("idpessoa") Long idpessoa) {
		
		pessoaRepository.deleteById(idpessoa);
		
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		modelAndView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
		
		// retornar um objeto vazio, pois o formulário(no cadastropessoa.html) está sendo preenchido para editar um Pessoa
		modelAndView.addObject("pessoa", new Pessoa()); 
	
		//carregando profissões do banco de dados para que esteja disponível na tela de Cadastro
		modelAndView.addObject("profissoes", profissaoRepository.findAll());
		
		return modelAndView;
	}
	
	@PostMapping(value = "**/pesquisarpessoa")
	public ModelAndView pesquisarPessoa(@RequestParam("nomepesquisa") String nomepesquisa, 
								        @PageableDefault(size = 5, sort = {"nome"}) Pageable pageable) {
		
		Page<Pessoa> pessoas =  pessoaRepository.procurarPessoaPorNomePage(nomepesquisa, pageable);
		
		ModelAndView modelAndView = new ModelAndView("cadastro/pesquisar-pessoas-por-nome");
		modelAndView.addObject("pessoasEncontradasPorNomeInformado", pessoas);
		modelAndView.addObject("nomepesquisa", nomepesquisa);
		
		
		return modelAndView;
	}
	
	
	@GetMapping(value = "adicionartelefones/{idpessoa}")
	public ModelAndView adicionarTelefones(@PathVariable("idpessoa") Long idpessoa) {
		
		Pessoa pessoa = pessoaRepository.findById(idpessoa).get();
		
		ModelAndView modelAndView = new ModelAndView("cadastro/adicionartelefone");
		modelAndView.addObject("pessoa", pessoa);
		modelAndView.addObject("telefones", telefoneRepository.getTelefones(idpessoa));
		return modelAndView;
	}
	
	@PostMapping(value = "**/addFonePessoa/{pessoaid}")
	public ModelAndView addFonePessoa(Telefone telefone , @PathVariable("pessoaid") Long pessoaid) {
		
		// consultar pessoa q tem o id que foi passado por parametro na requisição
		Pessoa pessoa = pessoaRepository.findById(pessoaid).get();
		
		
		if(telefone.getTipo().strip().isEmpty() || telefone.getNumero().strip().isEmpty()){
			   
			ModelAndView modelAndView = new ModelAndView("cadastro/adicionartelefone");
			modelAndView.addObject("pessoa", pessoa);
			modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoaid));
			
			modelAndView.addObject("mensagens", "Preencha os dados do telefone corretamente!");
			
			// if(telefone.getNumero().isEmpty() || telefone.getNumero() == null) 
			//	 modelAndView.addObject("mensagens", "Informe o número do telefone corretamente");
			// if(telefone.getTipo().isEmpty() || telefone.getTipo() == null) 
			//	 modelAndView.addObject("mensagens", "Informe o tipo do número corretamente");
			
			return modelAndView;
		}
		
		//atribuir essa pessoa ao telefone e salvar no banco de dados
		telefone.setPessoa(pessoa);
		telefoneRepository.save(telefone);
		
		ModelAndView modelAndView = new ModelAndView("cadastro/adicionartelefone");
		modelAndView.addObject("pessoa", pessoa);
		
		modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoaid));
		
		return modelAndView;
	}

	@GetMapping(value = "excluirtelefone/{idtelefone}")
	public ModelAndView excluirTelefone(@PathVariable("idtelefone") Long idtelefone) {
		
		// carregar a pessoa do telefone
		Pessoa pessoa = telefoneRepository.findById(idtelefone).get().getPessoa();
		
		// excluir
		telefoneRepository.deleteById(idtelefone);
		
		ModelAndView modelAndView = new ModelAndView("cadastro/adicionartelefone");
		modelAndView.addObject("pessoa", pessoa);
		modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoa.getId()));
		
		return modelAndView;
	}
	
	
	
	// redirecionar para página downloadpdfpessoas.html
	// downloadpdfpessoas.html é a página responsável pelo download das pessoas cadastrdas
	@RequestMapping(value = "**/download-pdf-pessoas-cadastradas")
	public ModelAndView redirecionarimprimir() {
		
		ModelAndView modelAndView = new ModelAndView("cadastro/downloadpdfpessoas");		
		return modelAndView;		
	}
	
	//redireciona para pagina pesquisar-pessoas-por-nome.html
	@RequestMapping(value = "**/pesquisar-pessoas-por-nome")
	public ModelAndView redirecionarPesquisaPorNome() {
		
		ModelAndView modelAndView = new ModelAndView("cadastro/pesquisar-pessoas-por-nome");	
		modelAndView.addObject("pessoasEncontradasPorNomeInformado", pessoaRepository.findAll(PageRequest.of(0, 5,Sort.by("nome"))));
		return modelAndView;		
	}
	
	@GetMapping(value = "**/downloadlistapessoas")
	public void downloadPdf(@RequestParam("nomedownload") String nomedownload, 
			HttpServletRequest request, HttpServletResponse response  ) {
		
		try {
		
			List<Pessoa> pessoas = new ArrayList<Pessoa>();
			
			if(nomedownload != null && !nomedownload.trim().isEmpty()) {
				pessoas = pessoaRepository.buscarPorNome(nomedownload);
			}else{
				
				// Iterable<Pessoa> itePessoas = pessoaRepository.findAll();
				
				// for (Pessoa pes : itePessoas) {
				//	pessoas.add(pes);
				// }
				
				pessoas = (List<Pessoa>) pessoaRepository.findAll();
			}
			
			// chamar o serviço que faz a geração do pdf para o download
			byte[] pdf = reportUtil.gerarRelatorio(pessoas, "pessoa", request.getServletContext());
			
			// informando o tamanho da resposta para o navegador 
			response.setContentLength(pdf.length);
			
			// Definir na respostao tipo do arquivo (serve para video, pdf, ...)
			response.setContentType("application/octet-stream");
			
			//Definir o cabeçalho da resposta
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"","relatorio.pdf");
			response.setHeader(headerKey, headerValue);
			
			// Finalizar a resposta
			response.getOutputStream().write(pdf);
			
		}catch (Exception e) {
			e.printStackTrace();	
		}
	}
	
	@GetMapping(value = "**/downloadcurriculo/{idpessoa}")
	public void downloadCurriculo(@PathVariable("idpessoa") Long idpessoa, HttpServletResponse response ) throws IOException {
		
		//consultar o objeto de pessoa no banco de dados
		Pessoa pessoa = pessoaRepository.findById(idpessoa).get();
		
		if(pessoa.getCurriculo() != null) {
			
			/*Setar o tamanho da resposta*/
			response.setContentLength(pessoa.getCurriculo().length);
			
			/*Tipo do arquivo para download*/
			/*O tipo do arquivo também poderia ser generico usando: response.setContentType("application/octet-stream"); */
			response.setContentType(pessoa.getTipoArquivoCurriculo());
			
			//Definir o cabeçalho da resposta
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", pessoa.getNomeArquivoCurriculo());
			response.setHeader(headerKey, headerValue);
			
			// Finalizar a resposta
			response.getOutputStream().write(pessoa.getCurriculo());
			
		}
		
	}
	
}


