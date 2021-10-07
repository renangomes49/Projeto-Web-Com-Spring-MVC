package br.com.springboot.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.springboot.model.Usuario;
import br.com.springboot.repository.UsuarioRepository;

@Service
@Transactional
public class ImplementacaoUserDetailsService implements UserDetailsService{

	@Autowired
	UsuarioRepository usuarioRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		Usuario usuario = usuarioRepository.findUserByLogin(username);
		
		if(usuario == null) {
			throw new UsernameNotFoundException("Usuário não foi encontrado");
		}
		
		return new org.springframework.security.core.userdetails.User(usuario.getLogin(), usuario.getPassword(), true, true, true, true, usuario.getAuthorities());
	}
	
	
	
	
}
