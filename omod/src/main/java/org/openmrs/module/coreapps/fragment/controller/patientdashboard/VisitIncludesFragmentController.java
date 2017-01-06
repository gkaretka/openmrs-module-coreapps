package org.openmrs.module.coreapps.fragment.controller.patientdashboard;

import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.VisitAttributeType;
import org.openmrs.Patient;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.coreapps.utils.VisitTypeHelper;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.ArrayList;
import java.util.List;

/**
 * VisitIncludes fragment. Retrieve and set the visit type and attributes
 */
public class VisitIncludesFragmentController {

	public void controller(FragmentConfiguration config,
			FragmentModel model,
			@InjectBeans PatientDomainWrapper wrapper,
			@SpringBean("adtService") AdtService adtService,
			@SpringBean("visitService") VisitService visitService,
			@SpringBean("visitTypeHelper") VisitTypeHelper visitTypeHelper,
			UiSessionContext sessionContext){

		config.require("patient");
		Object patient = config.get("patient");
		if (patient instanceof Patient) {
			wrapper.setPatient((Patient) patient);
		} else if (patient instanceof PatientDomainWrapper) {
			wrapper = (PatientDomainWrapper) patient;
		} else {
			throw new IllegalArgumentException("Patient must be of type Patient or PatientDomainWrapper");
		}

		model.addAttribute("patient", wrapper);
		List<VisitType> visitTypes = visitTypeHelper.getUnRetiredVisitTypes();

		// send active visits to the view, if any.
		List<Visit> activeVisits = visitService.getActiveVisitsByPatient(wrapper.getPatient());

		// get visit attributes
		List<VisitAttributeType> visitAttributeTypes = visitService.getAllVisitAttributeTypes();

		// get the current visit's visit type, if any active visit at the current visit location
		VisitDomainWrapper activeVisitWrapper = adtService.getActiveVisit(wrapper.getPatient(), sessionContext.getSessionLocation());
		if (activeVisitWrapper != null) {
			VisitType currentVisitType = activeVisitWrapper.getVisit().getVisitType();
			model.addAttribute("currentVisitType", currentVisitType);
		} else {
			model.addAttribute("currentVisitType", null);
		}

		model.addAttribute("activeVisits", activeVisits);
		model.addAttribute("visitTypes", visitTypes);
		model.addAttribute("visitAttributeTypes", visitAttributeTypes);
	}
}

