-- KEYS[1]=库存key  KEYS[2]=已抢用户集合key  ARGV[1]=userId  ARGV[2]=数量
if redis.call('sismember', KEYS[2], ARGV[1]) == 1 then
    return -1
end

local stock = tonumber(redis.call('get', KEYS[1]) or '0')
if stock < tonumber(ARGV[2]) then
    return 0
end

redis.call('decrby', KEYS[1], ARGV[2])
redis.call('sadd', KEYS[2], ARGV[1])
return 1
