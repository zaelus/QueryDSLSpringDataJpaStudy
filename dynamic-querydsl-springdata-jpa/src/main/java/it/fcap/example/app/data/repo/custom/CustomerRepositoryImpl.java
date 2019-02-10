package it.fcap.example.app.data.repo.custom;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import it.fcap.example.app.data.model.Customer;
import it.fcap.example.app.data.model.QCustomer;
import it.fcap.example.app.data.repo.custom.strategy.CustomerCriteriaStrategyEnum;
import it.fcap.example.app.data.search.CriteriaBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created by F.C. on 04/04/2017.
 */
public class CustomerRepositoryImpl implements CustomerRepositoryCustom<Customer> {

	@PersistenceContext
	private EntityManager em;

	@Override
	public Page<Customer> searchAllCustomerWithSearchBean(
			CriteriaBean criteria,
			Pageable pageRequest) {

		final String Q_ENTITY_RESULT = "customer";

		JPAQuery jpaQuery = new JPAQuery(em);

		QCustomer customer = new QCustomer(Q_ENTITY_RESULT);

		JPQLQuery<Customer> coreQuery = jpaQuery.from(customer);

		// SEARCH EXPRESSION + EVENTUALI JOIN
		BooleanExpression whereBody = this.buildAdvancedSearchExpression(
				customer, criteria, coreQuery,
				CustomerCriteriaStrategyEnum.values()
		);

		coreQuery = coreQuery.where(whereBody);

		// Costruisco la Paginazione
		Page<Customer> pageResult = this.buildPageForCustomersByQuery(
				pageRequest, coreQuery, Q_ENTITY_RESULT
		);

		return pageResult;
	}

	private BooleanExpression buildAdvancedSearchExpression(
			QCustomer rootQentity,
			CriteriaBean criteria,
			JPQLQuery<Customer> query,
			CustomerCriteriaStrategyEnum... args) {

		BooleanExpression result = rootQentity.eq(rootQentity);

		for (CustomerCriteriaStrategyEnum strategy: args) {
			strategy.addSearchCondition(rootQentity, query, criteria);
		}

		return result;
	}

	private Page<Customer> buildPageForCustomersByQuery(
			Pageable pageRequest,
			JPQLQuery<Customer> query, String qResultName
	) {

		for (Sort.Order o : pageRequest.getSort()) {
			PathBuilder orderByExpression = new PathBuilder(Object.class, qResultName);

			query.orderBy(new OrderSpecifier(o.isAscending() ? Order.ASC
					: Order.DESC, orderByExpression.get(o.getProperty())));
		}

		int pageNumber = pageRequest.getPageNumber();
		int pageSize = pageRequest.getPageSize();
		List<Customer> content = query.offset(pageNumber*pageSize).limit(pageSize).fetch();
		long count = query.fetchCount();
		return new PageImpl<Customer>(
				content, pageRequest, count
		);
	}
}