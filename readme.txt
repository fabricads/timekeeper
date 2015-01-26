
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


TODO:
- revisao do envers, guardar o login do usuario quem fez a ação
- todas operacoes rest, retornar uma resposta http, caso ocorra erro
- adicionar seguranca, por classe e metodo. autenticacao e autorizacao
- na seguranca, ao retornar objetos, para consultores, não pode retornar todos os atributos
- colocar um aviso na tela, caso não tenha conexões no endpoint rest.


git clone ssh://54b5c4603b696efd7e0004a5@brazil-consulting.itos.redhat.com/~/git/brazil.git/
cd brazil/

This will create a folder with the source code of your application. After making a change, add, commit, and push your changes.


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

--- problemas
* problemas na serializacao da relacao person.org e org.persons
* 


    01589   Brasília - DF   14/01   19:00   
    
