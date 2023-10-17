package com.gcpTest.gcp.database.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.gcpTest.gcp.database.model.Usuario;
import com.gcpTest.gcp.database.repository.CepRepository;
import com.gcpTest.gcp.database.repository.UsuarioRepository;
import com.google.gson.Gson;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController implements CommandLineRunner{

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private CepRepository cepRepository;
	
	@GetMapping("/list")
	public ResponseEntity<Object> getUsuarios() {

		List<Usuario> usuarios = null;

		try {

			usuarios = usuarioRepository.findAll();

		} catch (Exception e) {
			e.printStackTrace();
			return generateResponse("Erro ao Retornar Dados!", HttpStatus.BAD_REQUEST, usuarios);
		}

		return generateResponse("Dados Retornados com Sucesso!", HttpStatus.OK, usuarios);
	}

	@PostMapping("/save")
	public ResponseEntity<Object> save(@RequestBody Usuario usuario) {
		Usuario usuarioJ = new Usuario();
		Usuario usuarioProd = null;
		StringBuilder jsonCep = new StringBuilder();
		
		try {
			
			jsonCep = jsonReturn(usuario);
			
			Usuario usuarioAux = new Gson().fromJson(jsonCep.toString(), Usuario.class);
			
			usuarioJ.setNome(usuario.getNome());
			usuarioJ.setSobrenome(usuario.getSobrenome());
			usuarioJ.setEmail(usuario.getEmail());
			usuarioJ.setCep(usuarioAux.getCep());
			usuarioJ.setLogradouro(usuarioAux.getLogradouro());
			usuarioJ.setComplemento(usuarioAux.getComplemento());
			usuarioJ.setBairro(usuarioAux.getBairro());
			usuarioJ.setLocalidade(usuarioAux.getLocalidade());
			usuarioJ.setUf(usuarioAux.getUf());
			
			usuarioProd = usuarioRepository.save(usuarioJ);
			
		} catch (Exception e) {
			e.printStackTrace();

			return generateResponse("Erro ao Salvar Usuário!", HttpStatus.BAD_REQUEST, usuarioProd);
		}


		return generateResponse("Usuário Salvo com Sucesso!", HttpStatus.OK, usuarioProd);
	}

	@PostMapping("/saveRestTemplate")
	public ResponseEntity<Object> saveRestTemplate(@RequestBody Usuario usuario) {
 		Usuario usuarioJ = new Usuario();
		Usuario usuarioProd = null;
		ResponseEntity<Usuario> jsonCep = null;
		try {
			
			RestTemplate restTemplate = new RestTemplate();
			jsonCep = restTemplate.getForEntity("https://viacep.com.br/ws/"+usuario.getCep()+"/json/", Usuario.class);
			
			Usuario usuarioAux = new Gson().fromJson(jsonCep.getBody().toString().substring(7), Usuario.class);
			
			usuarioJ.setNome(usuario.getNome());
			usuarioJ.setSobrenome(usuario.getSobrenome());
			usuarioJ.setEmail(usuario.getEmail());
			usuarioJ.setCep(usuarioAux.getCep());
			usuarioJ.setLogradouro(usuarioAux.getLogradouro());
			usuarioJ.setComplemento(usuarioAux.getComplemento());
			usuarioJ.setBairro(usuarioAux.getBairro());
			usuarioJ.setLocalidade(usuarioAux.getLocalidade());
			usuarioJ.setUf(usuarioAux.getUf());
			
			usuarioProd = usuarioRepository.save(usuarioJ);
			
		} catch (Exception e) {
			e.printStackTrace();

			return generateResponse("Erro ao Salvar Usuário!", HttpStatus.BAD_REQUEST, usuarioProd);
		}


		return generateResponse("Usuário Salvo com Sucesso!", HttpStatus.OK, usuarioProd);
	}
	
	@PostMapping("/saveFeign")
	public ResponseEntity<Object> saveFeign(@RequestBody Usuario usuario) {
 		Usuario usuarioJ = new Usuario();
		Usuario usuarioProd = null;
		String jsonCep = null;
		try {
			
			jsonCep = cepRepository.getEnderecoPorCep(usuario.getCep());
			
			Usuario usuarioAux = new Gson().fromJson(jsonCep, Usuario.class);
			
			usuarioJ.setNome(usuario.getNome());
			usuarioJ.setSobrenome(usuario.getSobrenome());
			usuarioJ.setEmail(usuario.getEmail());
			usuarioJ.setCep(usuarioAux.getCep());
			usuarioJ.setLogradouro(usuarioAux.getLogradouro());
			usuarioJ.setComplemento(usuarioAux.getComplemento());
			usuarioJ.setBairro(usuarioAux.getBairro());
			usuarioJ.setLocalidade(usuarioAux.getLocalidade());
			usuarioJ.setUf(usuarioAux.getUf());
			
			usuarioProd = usuarioRepository.save(usuarioJ);
			
		} catch (Exception e) {
			e.printStackTrace();

			return generateResponse("Erro ao Salvar Usuário!", HttpStatus.BAD_REQUEST, usuarioProd);
		}


		return generateResponse("Usuário Salvo com Sucesso!", HttpStatus.OK, usuarioProd);
	}
	
	public static ResponseEntity<Object> generateResponse(String message, HttpStatus status, Object responseObj) {
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			map.put("message", message);
			map.put("status", status.value());
			map.put("data", responseObj);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Object>(status);

		}

		return new ResponseEntity<Object>(map,status);
	}
	
	//Conectando API EXTERNA JAVA PURO
	public StringBuilder jsonReturn(Usuario usuario) throws Exception {
		
		URL url = new URL("https://viacep.com.br/ws/"+usuario.getCep()+"/json/");
		URLConnection urlConnection = url.openConnection();
		InputStream is = urlConnection.getInputStream();
		InputStreamReader ir = new InputStreamReader(is,"UTF-8");
		BufferedReader br = new BufferedReader(ir);
		
		String cep = "";
		StringBuilder jsonCep = new StringBuilder();
		
		while ((cep = br.readLine()) != null) {
			jsonCep.append(cep);
		}
		
		return jsonCep;
	}

	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		
	}

}