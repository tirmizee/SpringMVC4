package com.tirmizee.backend.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;

import com.tirmizee.core.domain.Member;
import com.tirmizee.core.repository.ForgetPasswordRepository;
import com.tirmizee.core.repository.MemberRepositoryImpl;

@Repository("MemberDao")
public class MemberDaoImpl extends MemberRepositoryImpl implements MemberDao{

	@Override
	public Member findByUsername(String username) {
		try {
			StringBuilder select = new StringBuilder()
					.append("SELECT * FROM ").append(TABLE_MEMBER)
					.append(" WHERE ").append(COL_USERNAME).append(" = ?");
			return getJdbcOps().queryForObject(select.toString(), new Object[]{username}, ROW_MAPPER);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
		
	}

	@Override
	public Member findByEmail(String email) {
		try {
			StringBuilder select = new StringBuilder()
					.append("SELECT * FROM ").append(TABLE_MEMBER)
					.append(" WHERE ").append(COL_EMAIL).append(" = ?");
			return getJdbcOps().queryForObject(select.toString(), new Object[]{email}, ROW_MAPPER);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public Member findByToken(String token) {
		try {
			StringBuilder select = new StringBuilder()
				.append("SELECT ").append(TABLE_MEMBER).append(".*")
				.append(" FROM ").append(TABLE_MEMBER).append(" INNER JOIN ")
				.append(ForgetPasswordRepository.TABLE_FORGETPASSWORD)
				.append(" ON ").append(USERNAME).append(" = ")
				.append(ForgetPasswordRepository.USERNAME).append(" WHERE ")
				.append(ForgetPasswordRepository.TOKEN).append(" = ? ");
			return getJdbcOps().queryForObject(select.toString(), new Object[]{token}, ROW_MAPPER);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public Member findFirstLogin(String username) {
		try {
			StringBuilder select = new StringBuilder()
					.append("SELECT * FROM ").append(TABLE_MEMBER)
					.append(" WHERE ").append(COL_USERNAME).append(" = ? ")
					.append(" AND ").append(COL_INITIALLOGIN).append(" = 1 ");
			return getJdbcOps().queryForObject(select.toString(), new Object[]{username}, ROW_MAPPER);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

}
