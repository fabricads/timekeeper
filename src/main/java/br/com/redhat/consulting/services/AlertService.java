package br.com.redhat.consulting.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Asynchronous;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.redhat.consulting.model.Person;
import br.com.redhat.consulting.model.Project;
import br.com.redhat.consulting.model.Timecard;
import br.com.redhat.consulting.util.FreeMarkerUtil;

@Singleton
public class AlertService {
	
	@Inject
	private ProjectService projectService;
	
	@Inject
	private PersonService personService;
	
	
	
    private static Logger LOG = LoggerFactory.getLogger(AlertService.class);

    @Inject
    private EmailService emailService;
    
    
	
	@Schedule(dayOfWeek = "Fri", hour="16")
	private void alertManagerNoEntriesProjects(){
		LOG.debug("Schedule - Alert Manager no entries project");
		
		List<Project> projects = projectService.findNoEntriesThisWeekProjects();
		ManagerProjects previousManager = null;
		List<ManagerProjects> managers = new ArrayList<ManagerProjects>();
		
		for (Project project : projects) {
			if (previousManager == null){
				previousManager = new ManagerProjects();
				previousManager.setManager(project.getProjectManager());
				managers.add(previousManager);
			}
			
			if(previousManager.getManager().getId().intValue() == project.getProjectManager().getId().intValue()){
				previousManager.addProject(project);
			} else {
				previousManager = new ManagerProjects();
				previousManager.setManager(project.getProjectManager());
				previousManager.addProject(project);
				managers.add(previousManager);
			}
		}
		
		for (ManagerProjects managerProjects : managers) {
			sendNoEntriesEmail(managerProjects.getManager(), managerProjects.getProjects());
		}
		
	}
	
	
	private void sendNoEntriesEmail(Person manager, List<Project> projects){
		Map<String, Object> root = new HashMap<>();
        root.put("name", manager.getName());
        root.put("projects", projects);
        
        String text = FreeMarkerUtil.processTemplate("no_entries_week_project.ftl", root);
        if(text != null){
        	emailService.sendPlain(manager.getEmail(), "Projects with no entries this week", text);
        }
         
	}
	
	private class ManagerProjects{
		private Person manager;
		private List<Project> projects = new ArrayList<Project>();
		
		public Person getManager() {
			return manager;
		}
		public void setManager(Person manager) {
			this.manager = manager;
		}
		public List<Project> getProjects() {
			return projects;
		}
		public void addProject(Project project){
			this.projects.add(project);
		}
	}
	
	@Asynchronous
	public void alertSubmittedTimecard(Timecard timecard){
		Project project = timecard.getProject();
		Person manager = project.getProjectManager();
		Person consultant = timecard.getConsultant();
		
		Map<String, Object> root = new HashMap<>();
        root.put("managerName", manager.getName());
        root.put("consultantName", consultant.getName());
        root.put("projectName", project.getName());
        
        String text = FreeMarkerUtil.processTemplate("submitted_timecard.ftl", root);
        if(text != null){
        	emailService.sendPlain(manager.getEmail(), "Timecard submitted", text);
        }
	}
	
	@Schedule(dayOfWeek = "Fri", hour="8")
	private void alertConsultantWeeklyTimecard(){
		LOG.debug("Schedule - Alert Consultant weekly timecard");
		
		List<Person> findConsultantsInActiveProjects = personService.findConsultantsAndActiveProjects();
		for (Person consultant : findConsultantsInActiveProjects) {
			sendConsultantWeeklyTimecardEmail(consultant);
		}
	}
	
	private void sendConsultantWeeklyTimecardEmail(Person consultant){
		Map<String, Object> root = new HashMap<>();
        root.put("name", consultant.getName());
        root.put("projects", consultant.getProjects());
        
        
        String text = FreeMarkerUtil.processTemplate("consultant_weekly_timecard.ftl", root);
        if(text != null){
        	emailService.sendPlain(consultant.getEmail(), "Weekly timecard reminder", text);
        } 
	}
	
	@Schedule(hour="0", minute = "1") 
	private void disableJustEndedProjects(){
		LOG.debug("Schedule - Disable just ended projects");
		
		List<Project> projects = projectService.findAndDisableJustEndedProjects();
		for (Project project : projects) {
			sendJusEndedProjectEmail(project); 
		}
	}


	private void sendJusEndedProjectEmail(Project project) {
		Person manager = project.getProjectManager();
		Map<String, Object> root = new HashMap<>();
		root.put("name", manager.getName());
		root.put("project", project);
		
		String text = FreeMarkerUtil.processTemplate("just_ended_project.ftl", root);
		if(text != null){
			emailService.sendPlain(manager.getEmail(), "Project has just ended", text);
		}
	}
	

}
