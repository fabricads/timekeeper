
Requisitos - Colocar tudo em Inglês

cadastro de pessoas
- campos: Oralce PA ID, nome, email, empresa, país, estado, cidade de residência , telefone, senha, grupo

* cadastro de projetos 
- campos: nome, número PA, Project Manager, consultores associados

* suporte a permissões
- admin    : administração completa do sistema
- consultor: visualização do histórico de lançamentos de horas nos projetos associados

* relatórios
- geração de relatórios em formatos (csv, xls) por projeto, por consultor, por período

* histórico
- guardar histórico de operações: lançamentos, projetos e consultores

Todos os cadastros (pessoas, projetos), terão desativação, consultas, alteração, remoção.
Consultores poderão ser cadastrados apenas por convites de gerentes da Red Hat. Para não deixar aberto na web, para qualquer um se cadastrar.

Status: quando a hora é submetida, aparece S, quando aprovada A e ficar verde, quando reprovada R - vermelha;
Message: quando há um erro = ERROR
Type: vem escrito Contingent
Task: Task do projeto
Type: Labor

echo -n admin123 | openssl dgst -sha256 -binary | openssl base64

TODO:
- revisao do envers, guardar o login do usuario quem fez a ação
- colocar um aviso na tela, caso não tenha conexões no endpoint rest.

git clone ssh://54b5c4603b696efd7e0004a5@brazil-consulting.itos.redhat.com/~/git/brazil.git/
cd brazil/

- postgresql server
   Root User: admin6wwr82m
   Root Password: qg7NMPPRT6Ym
   Database Name: brazil
   
   usuário: timekeeper
   senha  : asQW12#$

Connection URL: postgresql://$OPENSHIFT_POSTGRESQL_DB_HOST:$OPENSHIFT_POSTGRESQL_DB_PORT

ssh 54b5c4603b696efd7e0004a5@brazil-consulting.itos.redhat.com

scp ~/tmp/jboss-eap-quickstarts-master/kitchensink/target/jboss-kitchensink.war  54b5c4603b696efd7e0004a5@brazil-consulting.itos.redhat.com:app-root/dependencies/jbosseap/deployments/
./jbosseap/bin/tools/jboss-cli.sh -c --controller=127.2.67.129
https://help.openshift.com/hc/en-us/articles/202399740-How-to-deploy-pre-compiled-java-applications-WAR-and-EAR-files-onto-your-OpenShift-gear-using-the-java-cartridges
http://brazil-consulting.itos.redhat.com/jboss-kitchensink/index.jsf

/subsystem=datasources/data-source=timekeeper:add(connection-url="jdbc:postgresql://${env.OPENSHIFT_POSTGRESQL_DB_HOST}:${env.OPENSHIFT_POSTGRESQL_DB_PORT}/brazil",check-valid-connection-sql="SELECT 1",driver-name=postgresql,jndi-name="java:/jdbc/partners_timekeeper",jta=true,password="asQW12#$",user-name=timekeeper)
/subsystem=datasources/data-source=timekeeper:enable
/subsystem=datasources/data-source=timekeeper:test-connection-in-pool

/subsystem=security/security-domain=timekeeper:add(cache-type=default)
/subsystem=security/security-domain=timekeeper/authentication=classic:add(login-modules=[{"code"=>"Database", "flag"=>"required", "module-options"=>[("dsJndiName"=>"java:/jdbc/partners_timekeeper"),("principalsQuery"=>"select password from person where enabled = true and email = ?"), ("rolesQuery"=>"select r.short_name, 'Roles' from role r inner join person p on p.id_role=r.id_role where p.email = ?"), ("hashAlgorithm"=>"SHA-256"),("hashEncoding"=>"base64")]}])

/system-property=timekeeper.host.address:add(value="https://brazil-consulting.itos.redhat.com")

insert into role(name,short_name) values('Partner Consultant','partner_consultant');
insert into role(name,short_name) values('Red Hat Manager','redhat_manager');
insert into partner_org(name,enabled) values('Red Hat',true);
insert into person(name, email,password,enabled,persontype,id_role,id_partner_org,city,country,state) values('Claudio Miranda','claudio@redhat.com','gOqk39ARU+xpdTuMv8/ZSVREd7X8EYS6H8v1vlekO5Y=', true,4,4,4,'Brasilia','Brasil','DF');

*** pendencias
- lembrador de senha na pagina de login
- editar projeto, coloca campo data com erro de preenchimento
- 
