package de.mq.portfolio.shareportfolio.support;

import java.util.Arrays;

import javax.faces.context.FacesContext;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

import de.mq.portfolio.support.SerialisationUtil;
import de.mq.portfolio.support.UserModel;

@Component
@Aspect
abstract class StateAspectImpl {

	@Autowired
	private SerialisationUtil serialisationUtil;

	@After("execution(* de.mq.portfolio.shareportfolio.support.AbstractPortfolioController.*(..)) && @annotation(de.mq.portfolio.support.Serialize) && args(portfolioSearchAO, ..))")
	void serialize(final PortfolioSearchAO portfolioSearchAO) {
		userModel().assign(facesContext().getViewRoot().getViewId(), serialisationUtil.serialize(serialisationUtil.toMap(portfolioSearchAO, Arrays.asList("name", "selectedPortfolioId"))));
	}

	@After("execution(* de.mq.portfolio.shareportfolio.support.AbstractPortfolioController.*(..))&& @annotation(de.mq.portfolio.support.DeSerialize)&& args(portfolioSearchAO,..) && target(controller)")
	void deSerialize(final PortfolioSearchAO portfolioSearchAO, final AbstractPortfolioController controller) {

		if (!portfolioSearchAO.isNew()) {

		}
		portfolioSearchAO.setUsed();
		if (userModel().state(facesContext().getViewRoot().getViewId()) == null) {
			return;
		}

		serialisationUtil.toBean(serialisationUtil.deSerialize(userModel().state(facesContext().getViewRoot().getViewId())), portfolioSearchAO);

		final String selectedPortfolioId = portfolioSearchAO.getSelectedPortfolioId();

		controller.page(portfolioSearchAO);

		portfolioSearchAO.getSharePortfolios().stream().filter(p -> p.id().equals(selectedPortfolioId)).findAny().ifPresent(selected -> portfolioSearchAO.setSelectedPortfolio(selected));

	}

	@Lookup
	abstract FacesContext facesContext();

	@Lookup
	abstract UserModel userModel();
}
