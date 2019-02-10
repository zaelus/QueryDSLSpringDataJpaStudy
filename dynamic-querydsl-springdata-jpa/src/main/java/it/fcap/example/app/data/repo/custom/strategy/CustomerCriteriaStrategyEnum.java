package it.fcap.example.app.data.repo.custom.strategy;

import com.querydsl.jpa.JPQLQuery;
import it.fcap.example.app.data.model.Customer;
import it.fcap.example.app.data.model.QCustomer;
import it.fcap.example.app.data.search.CriteriaBean;
import org.springframework.util.StringUtils;

/**
 * Created by F.C. on 04/04/2017.
 */
public enum CustomerCriteriaStrategyEnum {

	FIRST_NAME_SEARCH {
		@Override
		public void addSearchCondition(QCustomer rootQentity,
									   JPQLQuery<Customer> query,
									   CriteriaBean criteria) {
			if(criteria == null ||
					StringUtils.isEmpty(criteria.getFirstName())){
				// NOTHING TO DO
				return;
			}

			query.where(rootQentity.firstName.toLowerCase()
					.like("%" + criteria.getFirstName()
							.toLowerCase() + "%"));
		}
	},
	SECOND_NAME_SEARCH {
		@Override
		public void addSearchCondition(QCustomer rootQentity,
									   JPQLQuery<Customer> query,
									   CriteriaBean criteria) {
			if(criteria == null ||
					StringUtils.isEmpty(criteria.getLastName())){
				// NOTHING TO DO
				return;
			}

			query.where(rootQentity.lastName.toLowerCase()
					.like("%" + criteria.getLastName()
							.toLowerCase() + "%"));
		}
	};

	public abstract void addSearchCondition(QCustomer rootQentity,
											JPQLQuery<Customer> query,
											CriteriaBean criteria);
}