https://console.itos.redhat.com/console

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

https://help.openshift.com/hc/en-us/articles/202399740-How-to-deploy-pre-compiled-java-applications-WAR-and-EAR-files-onto-your-OpenShift-gear-using-the-java-cartridges

./jbosseap/bin/tools/jboss-cli.sh -c --controller=$OPENSHIFT_JBOSSEAP_IP

/subsystem=datasources/data-source=timekeeper:add(connection-url="jdbc:postgresql://${env.OPENSHIFT_POSTGRESQL_DB_HOST}:${env.OPENSHIFT_POSTGRESQL_DB_PORT}/brazil",check-valid-connection-sql="SELECT 1",driver-name=postgresql,jndi-name="java:/jdbc/partners_timekeeper",jta=true,password="asQW12#$",user-name=timekeeper)
/subsystem=datasources/data-source=timekeeper:enable
/subsystem=datasources/data-source=timekeeper:test-connection-in-pool

/subsystem=security/security-domain=timekeeper:add(cache-type=default)
/subsystem=security/security-domain=timekeeper/authentication=classic:add(login-modules=[{"code"=>"Database", "flag"=>"required", "module-options"=>[("dsJndiName"=>"java:/jdbc/partners_timekeeper"),("principalsQuery"=>"select password from person where enabled = true and email = ?"), ("rolesQuery"=>"select r.short_name, 'Roles' from role r inner join person p on p.id_role=r.id_role where p.email = ?"), ("hashAlgorithm"=>"SHA-256"),("hashEncoding"=>"base64")]}])

/system-property=timekeeper.host.address:add(value="https://brazil-consulting.itos.redhat.com")

insert into role(name,short_name) values('Partner Consultant','partner_consultant');
insert into role(name,short_name) values('Red Hat Manager','redhat_manager');
insert into role(name,short_name) values('Partner Manager','partner_manager');
insert into organization(name,enabled) values('Red Hat',true);
insert into person(name, email,password,enabled,person_type,id_role,id_org,city,country,state) values('Claudio Miranda','claudio@redhat.com','gOqk39ARU+xpdTuMv8/ZSVREd7X8EYS6H8v1vlekO5Y=', true,4,2,1,'Brasilia','Brasil','DF');

*** pendencias
- adicionar paginacao 
- adicionar filtros
- adicionar tela para consultas de entidades DISABLED
=================

tarefas pendentes, por ordem de implementação:

1) definir o tipo da task no cadastro.

As tasks devem ter classificação Labor ou Expense. Essa classificação é chamada Type.
viculação do consultor com task
O consultor deve ser cadastrado em uma task, e ao cair na tela de lançamento, aparecer somente a task que ele é cadastrado

2) lançamento de timecards.

Quando o consultor for lançar a hora, cair exatamente na semana corrente.
Permitir lançamentos para o consultor de 1 semana atrás e até 2 semanas para frente.

3) Alertas e tarefas background

O gerente de projetos deve receber um alerta do projeto dele, se não houverem horas lançadas no projeto até as 16 horas, todas as sextas feiras
Quando o consultor submeter horas para aprovação, o gerente do projeto deverá receber um e-mail avisando.
O consultor que estiver cadastradpo em um projeto ativo, deve receber alerta toda sexta feira, as 8 horas da manha, para lancar suas horas
Quando a data fim do projeto chegar, o projeto deve se tornar inativo e não permitir o lançamento de horas. Ter um campo de status do projeto na folha de cadastro (ativo ou inativo)


4) Vinculação de habilidades do consultor

Na tela de profile do consultor, ele deve informar o perfil dele (colocar um quadro com todas as tecnologias red hat as quais ele poderá marcar um X se tiver conhecimento)
Na tela de profile do consultor, ele pode informar as certificações dele

5) Campo de cliente no projeto

Ao adicionar um projeto, tem que ser informado o nome do cliente e esta informação tem que ser mostrada na página dos projetos.

6) cadastro de consultor

Não permitir cadastrar dois consultores com o mesmo email
Todo consultor ao ser criado, deve ser reconhecido por um número que será o mesmo do Oracle PA (Oracle Employee ID)
O consultor nao poderá lancar horas se pelo menos 1 campo de telefone, estiver preenchidos

7) Preenchimento de timecard

ao lançar hora, o consultor não pode lançar horas quebradas (4.19 ou 3.8). Ou lança 4 ou 4.5 (0,5 horas é a única opção).

8) Listagem de projetos

Na folha de rosto "projects" , aparecer o nome do PM
Na folha de rosto "project", permitir que a organização seja por PM assim como é por ordem alfabética (tipo um organizar por tipo)
Na folha de rosto do projeto, criar um filtro por PM, cliente, e Status do projeto (ativo ou inativo)

9) Relatórios

Os Relatórios de Saída para o gerente de projeto: 

anexar imagem do email.

10) Projetos com o mesmo número (ID do Projeto) podem ser salvos com nomes diferentes. Não permitir cadastrar dois projetos com o mesmo número.

