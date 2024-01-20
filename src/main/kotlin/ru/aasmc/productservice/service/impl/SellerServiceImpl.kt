package ru.aasmc.productservice.service.impl

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import ru.aasmc.productservice.dto.CreateSellerRequest
import ru.aasmc.productservice.dto.CreateSellerResponse
import ru.aasmc.productservice.dto.SellerResponse
import ru.aasmc.productservice.errors.ProductServiceException
import ru.aasmc.productservice.mapper.SellerMapper
import ru.aasmc.productservice.service.SellerService
import ru.aasmc.productservice.storage.repository.SellerRepository
import ru.aasmc.productservice.utils.CryptoTool

@Service
class SellerServiceImpl(
    private val sellerRepository: SellerRepository,
    private val mapper: SellerMapper,
    private val cryptoTool: CryptoTool
): SellerService {

    override fun createSeller(dto: CreateSellerRequest): CreateSellerResponse {
        val seller = sellerRepository.save(mapper.toDomain(dto))
        log.debug("Saved seller to DB: {}", seller)
        return mapper.toCreateDto(seller)
    }

    override fun getSellerById(hashedId: String): SellerResponse {
        val id = cryptoTool.idOf(hashedId)
        val seller = sellerRepository.findById(id)
            .orElseThrow {
                val msg = "Seller with ID=$hashedId not found"
                ProductServiceException(msg, HttpStatus.NOT_FOUND.value())
            }
        log.debug("Retrieved seller {} by id: {}.", seller, id)
        return mapper.toFindDto(seller)
    }

    companion object {
        private val log = LoggerFactory.getLogger(SellerServiceImpl::class.java)
    }

}