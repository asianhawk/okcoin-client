package org.oxerr.okcoin.rest;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.oxerr.okcoin.rest.domain.Depth;
import org.oxerr.okcoin.rest.domain.OrderResult;
import org.oxerr.okcoin.rest.domain.TickerResponse;
import org.oxerr.okcoin.rest.domain.Trade;
import org.oxerr.okcoin.rest.domain.TradeResult;
import org.oxerr.okcoin.rest.domain.UserInfo;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public interface OKCoin {

	@GET
	@Path("ticker.do")
	TickerResponse getTicker(@QueryParam("symbol") String symbol)
			throws IOException;

	@GET
	@Path("depth.do")
	Depth getDepth(@QueryParam("symbol") String symbol) throws IOException;

	@GET
	@Path("trades.do")
	Trade[] getTrades(@QueryParam("symbol") String symbol) throws IOException;

	@GET
	@Path("trades.do")
	Trade[] getTrades(@QueryParam("symbol") String symbol,
			@QueryParam("since") long since) throws IOException;

	@POST
	@Path("userinfo.do")
	UserInfo getUserInfo(@QueryParam("partner") long partner,
			@QueryParam("sign") String sign) throws IOException;

	@POST
	@Path("trade.do")
	TradeResult trade(@QueryParam("partner") long partner,
			@QueryParam("symbol") String symbol,
			@QueryParam("type") String type,
			@QueryParam("rate") String rate,
			@QueryParam("amount") String amount,
			@QueryParam("sign") String sign) throws IOException;

	@POST
	@Path("cancelorder.do")
	TradeResult cancelOrder(@QueryParam("partner") long partner,
			@QueryParam("order_id") long orderId,
			@QueryParam("symbol") String symbol,
			@QueryParam("sign") String sign) throws IOException;

	@POST
	@Path("getorder.do")
	OrderResult getOrder(@QueryParam("partner") long partner,
			@QueryParam("order_id") long orderId,
			@QueryParam("symbol") String symbol,
			@QueryParam("sign") String sign) throws IOException;

}