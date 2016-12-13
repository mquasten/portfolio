package de.mq.portfolio.shareportfolio.support;

import java.util.Arrays;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

import de.mq.portfolio.support.DeSerialize;
import de.mq.portfolio.support.Parameter;
import de.mq.portfolio.support.SerialisationUtil;
import de.mq.portfolio.support.Serialize;
import de.mq.portfolio.support.UserModel;

@Component
@Aspect
abstract class StateAspectImpl {

	@Autowired
	private SerialisationUtil serialisationUtil;

	@After("execution(* de.mq.portfolio.shareportfolio.support.AbstractPortfolioController.*(..)) && @annotation(de.mq.portfolio.support.Serialize) && args(portfolioSearchAO, ..) && @annotation(serialize))")
	void serialize(final PortfolioSearchAO portfolioSearchAO, final Serialize serialize) {
		userModel().assign(facesContext().getViewRoot().getViewId(), serialisationUtil.serialize(serialisationUtil.toMap(portfolioSearchAO, Arrays.asList(serialize.fields()), Arrays.asList(serialize.mappings()))));
	}

	@After("execution(* de.mq.portfolio.shareportfolio.support.AbstractPortfolioController.*(..))&& @annotation(de.mq.portfolio.support.DeSerialize)&& args(portfolioSearchAO,..) && target(controller)  && @annotation(deSerialize)  )")
	void deSerialize(final PortfolioSearchAO portfolioSearchAO, final AbstractPortfolioController controller, final DeSerialize deSerialize) {

		if (!portfolioSearchAO.isNew()) {
			return;
		}
		portfolioSearchAO.setUsed();
		if (userModel().state(facesContext().getViewRoot().getViewId()) == null) {
			return;
		}

		
		
		final Map<String, Object> stateMap = serialisationUtil.deSerialize(userModel().state(facesContext().getViewRoot().getViewId()));
		serialisationUtil.toBean(stateMap, portfolioSearchAO, Arrays.asList(deSerialize.mappings()));

		stateMap.put(Parameter.DEFAULT_PARAMETER, portfolioSearchAO);
		serialisationUtil.execute(controller, deSerialize.methodRegex(), stateMap);
		
	/*	final String selectedPortfolioId = portfolioSearchAO.getSelectedPortfolioId();

		controller.page(portfolioSearchAO);

		portfolioSearchAO.getSharePortfolios().stream().filter(p -> p.id().equals(selectedPortfolioId)).findAny().ifPresent(selected -> portfolioSearchAO.setSelectedPortfolio(selected)); */

	}

	@Lookup
	abstract FacesContext facesContext();

	@Lookup
	abstract UserModel userModel();
}
