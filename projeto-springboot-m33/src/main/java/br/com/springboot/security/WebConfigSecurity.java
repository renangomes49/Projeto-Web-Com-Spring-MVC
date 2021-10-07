package br.com.springboot.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

@Configuration
@EnableWebSecurity
@EnableWebMvc
public class WebConfigSecurity extends WebSecurityConfigurerAdapter{

	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService;
				
	
	
	 @Override //Configurar as solicitações de acesso por HTTP
	protected void configure(HttpSecurity http) throws Exception {

		 http.csrf()
		 .disable() // desabilita as configurações iniciais
		 .authorizeRequests() // permitir restringir acessos
		 .antMatchers(HttpMethod.GET, "/").permitAll()  // qualquer usuário pode acessar a página inicial
		// .antMatchers(HttpMethod.GET, "/cadastropessoa").permitAll()
		 .antMatchers(HttpMethod.GET, "/cadastropessoa").hasAnyRole("ADMIN","CAIXA")
		 .anyRequest().authenticated()
		 .and().formLogin().permitAll() // permite qualquer usuário
		 .loginPage("/login") // vai procurar a pagina login.html, não vai procurar a padrão
		 .defaultSuccessUrl("/cadastropessoa") // logou com sucesso é redirecionado para essa página
		 .failureUrl("/login?error=true") // falha ao logar redireciona para a página de login novamente
		 .and().logout() // mapeia URL de logout e invalida usuário
		 .logoutSuccessUrl("/login") //deslogou é redirecionado para a pagina de login
		 .logoutRequestMatcher(new AntPathRequestMatcher("/logout"));
	 
	}
	 
	 @Override // Cria Autenticação do usuário com banco de dados ou em memória
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		 auth.userDetailsService(implementacaoUserDetailsService)
		 .passwordEncoder(new BCryptPasswordEncoder());
		 
		 
		// auth.inMemoryAuthentication().passwordEncoder(NoOpPasswordEncoder.getInstance())
		// .withUser("renan")
		// .password("123")
		// .roles("ADMIN");
	 
	}
	 
	 
	@Override // Ignora URLs especificas
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/materialize/**");

	} 
	 
}


