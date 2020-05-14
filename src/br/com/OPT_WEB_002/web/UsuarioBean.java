package br.com.OPT_WEB_002.web;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.*;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.*;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.StreamedContent;
import br.com.OPT_WEB_002.documento.Documento;
import br.com.OPT_WEB_002.empresa.*;
import br.com.OPT_WEB_002.filial.*;
import br.com.OPT_WEB_002.tipo_documento.Tipo_Documento;
import br.com.OPT_WEB_002.tipo_documento.Tipo_DocumentoRN;
import br.com.OPT_WEB_002.unidade_negocio.*;
import br.com.OPT_WEB_002.usuario.*;
import br.com.OPT_WEB_002.util.DAOException;

@ManagedBean(name = "usuarioBean")
@SessionScoped
public class UsuarioBean {

	private List<SelectItem> idiomas;
	private Usuario usuarioSelecionado = new Usuario();
	private Usuario usuario = new Usuario();
	private Usuario usuarioLogin;
	private String confirmarSenha;
	private String senha = null;
	private String senhaNova = null;
	private String senhaAtual = null;
	private LazyDataModel<Usuario> lazymodel;
	private BigInteger id_doc;
	private Documento documento = new Documento();
	private Documento documentoSelecionado = new Documento();
	private StreamedContent streamedContent;
	private BigInteger id_usuario;
	private Empresa empresa = new Empresa();
	private Filial filial = new Filial();
	private Unidade_Negocio unidade_Negocio = new Unidade_Negocio();
	private String login = null;
	private UsuarioRN UsuarioRN = new UsuarioRN();
	private Tipo_Documento Tipo_Documento = new Tipo_Documento();
	
	@PostConstruct
	public void init() {
	}

	public UsuarioBean() {

	}
	
	public void cadastro(){
		
		EmpresaRN empresaRN = new EmpresaRN();
		Empresa empresa = new Empresa();
		
		FilialRN filialRN = new FilialRN();
		Filial filial = new Filial();
		
		Unidade_NegocioRN unidade_NegocioRN = new Unidade_NegocioRN();
		Unidade_Negocio unidade_Negocio = new Unidade_Negocio();
						
		UsuarioRN usuarioRN = new UsuarioRN();
		Usuario usuario = new Usuario();
	
		empresa.setCod_empresa(1);
		try {
			empresaRN.salvar(empresa);
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		filial.setCod_filial(1);
		filial.getCod_empresa().setCod_empresa(1);
		try {
			filialRN.salvar(filial);
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		unidade_Negocio.setCod_unidade(1);
		unidade_Negocio.getCod_empresa().setCod_empresa(1);
		try {
			unidade_NegocioRN.salvar(unidade_Negocio);
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		usuario.getCod_empresa().setCod_empresa(1);
		usuario.getCod_filial().setCod_filial(1);
		usuario.getCod_unidade().setCod_unidade(1);
		usuario.setEmail("usuarioadm@hotmail.com");
		usuario.setLogin("001");
		usuario.setNome("usuarioadm");
		usuario.setSenha("001");
		usuario.setTipo_usuario(true);
		try {
			usuarioRN.salvar(usuario);
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public String Login() throws DAOException {

		UsuarioRN usuarioRN = new UsuarioRN();
		
		usuarioLogin = usuarioRN.login(login, senha);
		
		if(usuarioLogin == null){
			
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR,"","usuário ou senha incorreto!"));
			return null;
			
		}else{
			
			return "/restrito/menu.xhtml?faces-redirect=true";
		}
		

	}
	
	public boolean permissaoAcoesUsuario(boolean permissao) {
		
		if(permissao) {
			
			return false;
		}else {
			
			return true;
		}
		
	}


	public Empresa retornoDeEmpresaSelecionada() {

		return this.empresa;
	}

	public boolean autorizarUsuario() {

		retornaImagemEmpresa();
		
		try {

			if (usuarioLogin.isTipo_usuario()) {

				return true;
			} else {
				return false;
			}

		} catch (NullPointerException e) {
            
		
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning!", e.getMessage()));

			e.printStackTrace();
			
		}
		
		return false;
	}

	 public boolean permissaoParaAdministrador() {

		if (usuarioLogin != null && usuarioLogin.getPermissao().toString().contains("ADMINISTRADOR")) {

			return false;

		} else {

			return true;
		}
	}

	public Usuario iniciar() {

		UsuarioRN usuarioRN = new UsuarioRN();

		if (id_usuario == null) {

			return this.usuario;

		} else {
		
			this.usuario = usuarioRN.carregar(id_usuario);
          
			return this.usuario;
		}
	}
	
	
	
	
	public String editarSenhaUsuario(){
		
		UsuarioRN usuarioRN = new UsuarioRN();
				
		if(usuarioLogin.getSenha().equals(senhaAtual)){
			
			this.usuario = usuarioRN.carregar(usuarioLogin.getId_usuario());
							
			if (!this.senhaNova.equals(this.confirmarSenha)) {

				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning!", "A senha não foi confirmada corretamente!"));
				return null;
			
			}else{
				
				this.usuario.setSenha(senhaNova);				
				usuarioRN.alterar(this.usuario);
			
				senhaAtual = null;
				senhaNova = null;
				confirmarSenha = null;
				
				return "/res"
						+ "trito/menu.xhtml";
			}
			
		}else{

			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning!", "senha atual incorreta!"));
			return null;
		
		}

	}

	public List<SelectItem> getIdiomas() {

		if (this.idiomas == null) {

			this.idiomas = new ArrayList<SelectItem>();

			this.idiomas.add(new SelectItem("pt_BR", "Portugues"));
			this.idiomas.add(new SelectItem("en_US", "English"));
			this.idiomas.add(new SelectItem("es_ES", "Espanhol"));
		}

		return idiomas;
	}

	public String salvar() {

		UsuarioRN usuarioRN = new UsuarioRN();

		if (id_usuario == null) {

			try {

				FacesContext mensagemErroLogin = FacesContext.getCurrentInstance();
				String senha = iniciar().getSenha();

				if (!senha.equals(this.confirmarSenha)) {

					FacesMessage msg = new FacesMessage("A senha não foi confirmada corretamente");
					mensagemErroLogin.addMessage(null, msg);

					return null;
				}

				this.usuario.getCod_empresa().setCod_empresa(this.empresa.getCod_empresa());
				this.usuario.getCod_filial().setCod_filial(this.filial.getCod_filial());
				this.usuario.getCod_unidade().setCod_unidade(this.unidade_Negocio.getCod_unidade());
		       
				usuarioRN.salvar(this.usuario);

				this.usuario = null;
				this.usuario = new Usuario();

				this.usuarioSelecionado = null;
				this.usuarioSelecionado = new Usuario();

				confirmarSenha = null;
				id_usuario = null;

			} catch (DAOException e) {

				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning!", e.getMessage()));
				return null;
			}

			return "/restrito/usuario/usuario.xhtml?faces-redirect=true";

		} else {

			FilialRN filialRN = new FilialRN();
			EmpresaRN empresaRN = new EmpresaRN();
			Unidade_NegocioRN unNegocioRN = new Unidade_NegocioRN();
		

			this.usuario.setCod_empresa(empresaRN.carregar(this.empresa.getCod_empresa()));
			this.usuario.setCod_filial(filialRN.carregar(this.filial.getCod_filial()));
			this.usuario.setCod_unidade(unNegocioRN.carregar(this.unidade_Negocio.getCod_unidade()));
			
	
			usuarioRN.alterar(this.usuario);
			
			this.usuarioLogin = this.usuario;

			this.usuario = null;
			this.usuario = new Usuario();

			this.usuarioSelecionado = null;
			this.usuarioSelecionado = new Usuario();

			confirmarSenha = null;
			id_usuario = null;

			return "/restrito/usuario/usuario.xhtml?faces-redirect=true";
		}

	}

	public void excluir() {

		UsuarioRN usuarioRN = new UsuarioRN();

		try {

			this.usuario = usuarioRN.carregar(usuarioSelecionado.getId_usuario());
			usuarioRN.excluir(this.usuario);

			this.usuario = null;
			this.usuario = new Usuario();

			this.usuarioSelecionado = null;
			this.usuarioSelecionado = new Usuario();

			confirmarSenha = null;
			id_usuario = null;

		} catch (DAOException e) {

			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning!", e.getMessage()));

		}

	}

	public String ativar() {

		if (this.usuario.isAtivo()) {

			this.usuario.setAtivo(false);

		} else {

			this.usuario.setAtivo(true);
		}

		UsuarioRN usuarioRN = new UsuarioRN();

		try {
			usuarioRN.salvar(this.usuario);
		} catch (DAOException e) {
			e.printStackTrace();
		}
		return null;

	}

	public List<Usuario> listar() {

		UsuarioRN usuarioRN = new UsuarioRN();

		if (permissaoParaAdministrador() == false) {

			return usuarioRN.listar();

		} else {

			return usuarioRN.listarPorCodEmpresa(usuarioLogin.getCod_empresa().getCod_empresa());

		}
	}

	public String atribuiPermissao(Usuario usuario, String permissao) {

		this.usuario = usuario;

		Set<String> permissoes = this.usuario.getPermissao();

		if (permissoes.contains(permissao)) {

			permissoes.remove(permissao);

		} else {

			permissoes.add(permissao);

		}
		return null;

	}

	public String novo() {

		return "/restrito/usuario/cadastro_usuario.xhtml?faces-redirect=true";

	}

	public String alterar() {

		return "/restrito/usuario/cadastro_usuario.xhtml?id=" + usuarioSelecionado.getId_usuario()
				+ "faces-redirect=true";
	}

	public boolean desabilitarCampo() {

		if (id_usuario != null) {

			return true;
		}

		return false;
	}

	public boolean DesabilitarSenhaConfirmada() {

		if (id_usuario != null) {

			return false;
		} else {

			return true;
		}

	}

	public String redirecionaMenu() {
		
		

		this.usuario = null;
		this.usuario = new Usuario();
				
		this.usuarioSelecionado = null;
		this.usuarioSelecionado = new Usuario();

		confirmarSenha = null;
		id_usuario = null;

		return "/restrito/menu.xhtml?faces-redirect=true";
	}

	public String descricaoTipoUsuario(boolean tipo_usuario) {

		if (tipo_usuario) {

			return "Interno";
		} else {
			return "Externo";
		}
	}
	
	public void retornaImagemEmpresa() {
		
		EmpresaRN empresaRN = new EmpresaRN();
		Empresa empresa = new Empresa();
	   
		try{
		
			empresa = empresaRN.carregar(usuarioLogin.getCod_empresa().getCod_empresa());
	
			if(empresa.getLogotipo() != null){
			
				InputStream in = new ByteArrayInputStream(empresa.getLogotipo());	
				this.streamedContent = new DefaultStreamedContent(in,empresa.getExtensaoArq(),empresa.getNomeArq());				
			}
			
		}catch(Exception e){
			
			e.getStackTrace();
			
		}
			
	}
	
	public List<Usuario> listarPorCodEmFiCodUni(){
		
		return UsuarioRN.listarPorCodEmCoFiCodUni(usuarioLogin.getCod_empresa().getCod_empresa(),usuarioLogin.getCod_filial().getCod_filial(),usuarioLogin.getCod_unidade().getCod_unidade());
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getConfirmarSenha() {
		return confirmarSenha;
	}

	public void setConfirmarSenha(String confirmarSenha) {

		this.confirmarSenha = confirmarSenha;
	}

	public void setIdiomas(List<SelectItem> idiomas) {
		this.idiomas = idiomas;
	}

	public Usuario getUsuarioSelecionado() {
		return usuarioSelecionado;
	}

	public void setUsuarioSelecionado(Usuario usuarioSelecionado) {
		this.usuarioSelecionado = usuarioSelecionado;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public LazyDataModel<Usuario> getDatamodel() {
		return lazymodel;
	}

	public void setDatamodel(LazyDataModel<Usuario> lazymodel) {
		this.lazymodel = lazymodel;
	}

	public LazyDataModel<Usuario> getLazymodel() {
		return lazymodel;
	}

	public void setLazymodel(LazyDataModel<Usuario> lazymodel) {
		this.lazymodel = lazymodel;
	}

	public Documento getDocumentoSelecionado() {
		return documentoSelecionado;
	}

	public void setDocumentoSelecionado(Documento documentoSelecionado) {
		this.documentoSelecionado = documentoSelecionado;
	}

	public Documento getDocumento() {
		return documento;
	}

	public void setDocumento(Documento documento) {
		this.documento = documento;
	}

	public StreamedContent getStreamedContent() {
		return streamedContent;
	}

	public void setStreamedContent(StreamedContent streamedContent) {
		this.streamedContent = streamedContent;
	}

	public BigInteger getId_doc() {
		return id_doc;
	}

	public void setId_doc(BigInteger id_doc) {
		this.id_doc = id_doc;
	}

	public BigInteger getId_usuario() {
		return id_usuario;
	}

	public void setId_usuario(BigInteger id_usuario) {
		this.id_usuario = id_usuario;
	}

	public Filial getFilial() {

		this.filial.setCod_filial(iniciar().getCod_filial().getCod_filial());

		return filial;
	}

	public void setFilial(Filial filial) {
		this.filial = filial;
	}

	public Empresa getEmpresa() {

		this.empresa.setCod_empresa(iniciar().getCod_empresa().getCod_empresa());

		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public Unidade_Negocio getUnidade_Negocio() {

		this.unidade_Negocio.setCod_unidade(iniciar().getCod_unidade().getCod_unidade());

		return unidade_Negocio;
	}

	public void setUnidade_Negocio(Unidade_Negocio unidade_Negocio) {
		this.unidade_Negocio = unidade_Negocio;
	}

	public Usuario getUsuarioLogin() {
		return usuarioLogin;
	}

	public void setUsuarioLogin(Usuario usuarioLogin) {
		this.usuarioLogin = usuarioLogin;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public UsuarioRN getUsuarioRN() {
		return UsuarioRN;
	}

	public void setUsuarioRN(UsuarioRN usuarioRN) {
		UsuarioRN = usuarioRN;
	}

	public String getSenhaNova() {
		return senhaNova;
	}

	public void setSenhaNova(String senhaNova) {
		this.senhaNova = senhaNova;
	}

	public String getSenhaAtual() {
		return senhaAtual;
	}

	public void setSenhaAtual(String senhaAtual) {
		this.senhaAtual = senhaAtual;
	}

	public Tipo_Documento getTipo_Documento() {
		return Tipo_Documento;
	}

	public void setTipo_Documento(Tipo_Documento tipo_Documento) {
		Tipo_Documento = tipo_Documento;
	}

}